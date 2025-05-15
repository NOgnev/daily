package com.klaxon.daily.controller.api;

import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.dto.AuthUser;
import com.klaxon.daily.dto.DialogItem;
import com.klaxon.daily.repository.DailyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/daily")
@RequiredArgsConstructor
public class DailyController {

    private final DailyRepository dailyRepository;

    @Log(logResult = false)
    @GetMapping("/dialog")
    public ResponseEntity<List<DialogItem>> getDialog(@AuthenticationPrincipal AuthUser userDetails,
                                                      @RequestParam LocalDate date) {
        List<DialogItem> dialogItems = dailyRepository.findAllByUserIdAndDate(userDetails.id(), date);
        return ResponseEntity.ok(dialogItems);
    }
    @Log(logResult = false)
    @PostMapping("/dialog")
    public ResponseEntity<List<DialogItem>> next(@AuthenticationPrincipal AuthUser userDetails,
                                                 @RequestParam LocalDate date,
                                                 @RequestParam String content) {
        List<DialogItem> dialogItems = dailyRepository.findAllByUserIdAndDate(userDetails.id(), date);
        var last = dialogItems.getLast();
        var res = dailyRepository.addDialogItem(userDetails.id(), date,
                DialogItem.builder()
                        .id(last.id() + 1)
                        .role(DialogItem.Role.USER)
                        .type(DialogItem.Type.ANSWER)
                        .content(content)
                        .build()
        );
        return ResponseEntity.ok(res);
    }
}
