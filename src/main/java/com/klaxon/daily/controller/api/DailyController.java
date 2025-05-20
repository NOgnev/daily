package com.klaxon.daily.controller.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.dto.AuthUser;
import com.klaxon.daily.dto.Dialog;
import com.klaxon.daily.dto.DialogItem;
import com.klaxon.daily.error.AppException;
import com.klaxon.daily.error.ErrorRegistry;
import com.klaxon.daily.repository.DailyRepository;
import com.klaxon.daily.service.client.OpenAiClient;
import com.klaxon.daily.service.client.OpenAiClient.ChatRequest.Message;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.klaxon.daily.dto.Dialog.Status.ERROR;
import static com.klaxon.daily.dto.Dialog.Status.FINISHED;
import static com.klaxon.daily.dto.Dialog.Status.IN_PROGRESS;
import static com.klaxon.daily.dto.DialogItem.Role.ASSISTANT;
import static com.klaxon.daily.dto.DialogItem.Role.SYSTEM;
import static com.klaxon.daily.dto.DialogItem.Role.USER;
import static com.klaxon.daily.dto.DialogItem.Type.ANSWER;
import static com.klaxon.daily.dto.DialogItem.Type.QUESTION;

@Slf4j
@Validated
@RestController
@RequestMapping("/api/daily")
@RequiredArgsConstructor
public class DailyController {

    @Value("${client.openai.moderation.model}")
    private String moderationModel;
    @Value("${client.openai.chat.model}")
    private String chatModel;
    @Value("${client.openai.chat.prompt}")
    private String chatPrompt;
    @Value("${client.openai.chat.temperature}")
    private double temperature;
    @Value("${client.openai.chat.max-tokens}")
    private int maxTokens;


    private final DailyRepository dailyRepository;
    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    @Log(logResult = false)
    @GetMapping("/dialog")
    public ResponseEntity<List<DialogItem>> getDialog(@AuthenticationPrincipal AuthUser userDetails,
                                                      @RequestParam LocalDate date) {
        List<DialogItem> dialogItems;
        try {
            dialogItems = dailyRepository.findMessagesByUserIdAndDate(userDetails.id(), date);
        } catch (EmptyResultDataAccessException e) {
            dialogItems = List.of();
        }
        return ResponseEntity.ok(dialogItems);
    }

    @Log(logResult = false)
    @PostMapping("/dialog")
    public ResponseEntity<List<DialogItem>> nextStep(@AuthenticationPrincipal AuthUser userDetails,
                                                     @RequestParam LocalDate date,
                                                     @RequestParam(required = false) String content) {

        Dialog dialog;
        try {
            dialog = dailyRepository.findByUserIdAndDate(userDetails.id(), date);
        } catch (EmptyResultDataAccessException e) {
            dialog = null;
        }

        if (dialog == null) {
            var request = OpenAiClient.ChatRequest.builder()
                    .model(chatModel)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .messages(List.of(Message.builder().role(SYSTEM).content(chatPrompt).build()))
                    .build();
            var response = openAiClient.chat(request);
            var message = response.choices().getFirst().message();
            DialogItem.Type type = message.content().type();
            var dialogItem = DialogItem.builder()
                    .id(1)
                    .role(message.role())
                    .type(type)
                    .content(message.content().content())
                    .build();
            return ResponseEntity.ok(dailyRepository.addDialogItem(userDetails.id(), date, dialogItem,
                    type == DialogItem.Type.FINAL ? FINISHED : IN_PROGRESS, message.content().summary()));
        } else {
            if (content == null) {
                throw AppException.builder()
                        .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .error(ErrorRegistry.CONTENT_NEEDED)
                        .build();
            }
            if (dialog.status().equals(FINISHED) || dialog.status().equals(ERROR)) {
                throw AppException.builder()
                        .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .error(ErrorRegistry.DIALOG_FINISHED)
                        .build();
            }
            var res = openAiClient.moderate(OpenAiClient.ModerationRequest.builder()
                    .model(moderationModel)
                    .input(content)
                    .build()
            );
            var result = res.results().get(0);
            if (result.flagged()) {
                throw AppException.builder()
                        .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .error(ErrorRegistry.FORBIDDEN_CONTENT)
                        .build();
            }
            var messages = dailyRepository.addDialogItem(userDetails.id(), date, DialogItem.builder()
                    .id(dialog.messages().size() + 1)
                    .role(USER)
                    .type(ANSWER)
                    .content(content)
                    .build(), IN_PROGRESS, null);
            String prompt = chatPrompt;
            var summaries = dailyRepository.getSummaries(userDetails.id(), date).stream()
                    .filter(summary -> !(summary.summary() == null || summary.summary().isBlank()))
                    .toList();
            try {
                prompt = String.format(prompt, objectMapper.writeValueAsString(summaries));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            List<Message> reqMessages = new ArrayList<>();
            reqMessages.add(Message.builder().role(SYSTEM).content(prompt).build());
            reqMessages.addAll(messages.stream()
                    .map(item -> Message.builder()
                            .role(item.role())
                            .content(item.role().equals(ASSISTANT) ? buildContent(item) : item.content())
                            .build())
                    .toList());
            var request = OpenAiClient.ChatRequest.builder()
                    .model(chatModel)
                    .temperature(temperature)
                    .maxTokens(maxTokens)
                    .messages(reqMessages)
                    .build();

            var response = openAiClient.chat(request);
            log.info(response.toString());
            var message = response.choices().getFirst().message();
            DialogItem.Type type = message.content().type();
            var dialogItem = DialogItem.builder()
                    .id(messages.size() + 1)
                    .role(message.role())
                    .type(type)
                    .content(message.content().content())
                    .build();
            return ResponseEntity.ok(dailyRepository.addDialogItem(userDetails.id(), date, dialogItem,
                    type == DialogItem.Type.FINAL ? FINISHED : IN_PROGRESS, message.content().summary()));
        }
    }

    @SneakyThrows
    private String buildContent(DialogItem item) {
        return objectMapper.writeValueAsString(OpenAiClient.ChatResponse.Choice.Message.Content.builder()
                .type(QUESTION)
                .content(item.content())
                .build());
    }
}
