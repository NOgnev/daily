package com.klaxon.daily.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.dto.Dialog;
import com.klaxon.daily.dto.DialogItem;
import com.klaxon.daily.error.AppException;
import com.klaxon.daily.repository.DailyRepository;
import com.klaxon.daily.service.client.OpenAiClient;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.klaxon.daily.dto.Dialog.Status.ERROR;
import static com.klaxon.daily.dto.Dialog.Status.FINISHED;
import static com.klaxon.daily.dto.Dialog.Status.IN_PROGRESS;
import static com.klaxon.daily.dto.DialogItem.Role.ASSISTANT;
import static com.klaxon.daily.dto.DialogItem.Role.SYSTEM;
import static com.klaxon.daily.dto.DialogItem.Role.USER;
import static com.klaxon.daily.dto.DialogItem.Type.ANSWER;
import static com.klaxon.daily.dto.DialogItem.Type.QUESTION;
import static com.klaxon.daily.error.ErrorRegistry.CONTENT_NEEDED;
import static com.klaxon.daily.error.ErrorRegistry.DIALOG_FINISHED;
import static com.klaxon.daily.error.ErrorRegistry.FORBIDDEN_CONTENT;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyService {

    private final DailyRepository dailyRepository;
    private final ObjectMapper objectMapper;
    private final OpenAiClient openAiClient;

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

    @Log
    public List<DialogItem> getDialog(UUID userId, LocalDate date) {
        List<DialogItem> dialogItems;
        try {
            dialogItems = dailyRepository.findMessagesByUserIdAndDate(userId, date);
        } catch (EmptyResultDataAccessException e) {
            dialogItems = List.of();
        }
        return dialogItems;
    }

    @Log
    public List<DialogItem> nextStep(UUID userId, LocalDate date, String content) {
        Dialog dialog = getDialogOrNull(userId, date);

        if (dialog == null) {
            return startNewDialog(userId, date);
        }

        validateDialogState(dialog, content);
        moderateContent(content);
        List<DialogItem> messages = addUserMessage(userId, date, dialog, content);
        String prompt = buildPrompt(userId, date);
        List<OpenAiClient.ChatRequest.Message> reqMessages = buildRequestMessages(prompt, messages);

        var response = openAiClient.chat(buildChatRequest(reqMessages));
        log.info(response.toString());

        var message = response.choices().getFirst().message();
        DialogItem dialogItem = buildDialogItem(messages.size() + 1, message);

        return dailyRepository.addDialogItem(
                userId, date, dialogItem,
                message.content().type() == DialogItem.Type.FINAL ? FINISHED : IN_PROGRESS,
                message.content().summary()
        );
    }

    private Dialog getDialogOrNull(UUID userId, LocalDate date) {
        try {
            return dailyRepository.findByUserIdAndDate(userId, date);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    private List<DialogItem> startNewDialog(UUID userId, LocalDate date) {
        var systemMessage = OpenAiClient.ChatRequest.Message.builder()
                .role(SYSTEM)
                .content(chatPrompt)
                .build();

        var response = openAiClient.chat(OpenAiClient.ChatRequest.builder()
                .model(chatModel)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .messages(List.of(systemMessage))
                .build());

        var message = response.choices().getFirst().message();
        DialogItem dialogItem = buildDialogItem(1, message);

        return dailyRepository.addDialogItem(
                userId, date, dialogItem,
                dialogItem.type() == DialogItem.Type.FINAL ? FINISHED : IN_PROGRESS,
                message.content().summary()
        );
    }

    private void validateDialogState(Dialog dialog, String content) {
        if (content == null) {
            throw AppException.builder()
                    .httpStatus(UNPROCESSABLE_ENTITY)
                    .error(CONTENT_NEEDED)
                    .build();
        }

        if (dialog.status().equals(FINISHED) || dialog.status().equals(ERROR)) {
            throw AppException.builder()
                    .httpStatus(UNPROCESSABLE_ENTITY)
                    .error(DIALOG_FINISHED)
                    .build();
        }
    }

    private void moderateContent(String content) {
        var res = openAiClient.moderate(OpenAiClient.ModerationRequest.builder()
                .model(moderationModel)
                .input(content)
                .build());

        if (res.results().getFirst().flagged()) {
            throw AppException.builder()
                    .httpStatus(UNPROCESSABLE_ENTITY)
                    .error(FORBIDDEN_CONTENT)
                    .build();
        }
    }

    private List<DialogItem> addUserMessage(UUID userId, LocalDate date, Dialog dialog, String content) {
        return dailyRepository.addDialogItem(userId, date, DialogItem.builder()
                .id(dialog.messages().size() + 1)
                .role(USER)
                .type(ANSWER)
                .content(content)
                .build(), IN_PROGRESS, null);
    }

    private String buildPrompt(UUID userId, LocalDate date) {
        var summaries = dailyRepository.getSummaries(userId, date).stream()
                .filter(summary -> summary.summary() != null && !summary.summary().isBlank())
                .toList();

        try {
            return String.format(chatPrompt, objectMapper.writeValueAsString(summaries));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to build prompt", e);
        }
    }

    private List<OpenAiClient.ChatRequest.Message> buildRequestMessages(String prompt, List<DialogItem> messages) {
        List<OpenAiClient.ChatRequest.Message> result = new ArrayList<>();
        result.add(OpenAiClient.ChatRequest.Message.builder().role(SYSTEM).content(prompt).build());

        result.addAll(messages.stream()
                .map(item -> OpenAiClient.ChatRequest.Message.builder()
                        .role(item.role())
                        .content(item.role().equals(ASSISTANT) ? buildContent(item) : item.content())
                        .build())
                .toList());

        return result;
    }

    private OpenAiClient.ChatRequest buildChatRequest(List<OpenAiClient.ChatRequest.Message> messages) {
        return OpenAiClient.ChatRequest.builder()
                .model(chatModel)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .messages(messages)
                .build();
    }

    private DialogItem buildDialogItem(int id, OpenAiClient.ChatResponse.Choice.Message message) {
        return DialogItem.builder()
                .id(id)
                .role(message.role())
                .type(message.content().type())
                .content(message.content().content())
                .build();
    }

    @SneakyThrows
    private String buildContent(DialogItem item) {
        return objectMapper.writeValueAsString(OpenAiClient.ChatResponse.Choice.Message.Content.builder()
                .type(QUESTION)
                .content(item.content())
                .build());
    }
}
