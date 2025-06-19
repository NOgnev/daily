package com.klaxon.daily.controller.api;

import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.dto.AuthUser;
import com.klaxon.daily.dto.DialogItem;
import com.klaxon.daily.service.DailyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Validated
@RestController
@RequestMapping("/api/daily")
@RequiredArgsConstructor
public class DailyController {

    private final DailyService dailyService;

    @Log(logResult = false)
    @GetMapping("/dialog")
    public ResponseEntity<List<DialogItem>> getDialog(@AuthenticationPrincipal AuthUser userDetails,
                                                      @RequestParam LocalDate date) {
        return ResponseEntity.ok(dailyService.getDialog(userDetails.id(), date));
    }

    @Log(logResult = false)
    @PostMapping("/dialog")
    public ResponseEntity<List<DialogItem>> nextStep(@AuthenticationPrincipal AuthUser userDetails,
                                                     @RequestParam LocalDate date,
                                                     @RequestParam(required = false) String content) {
        return ResponseEntity.ok().body(dailyService.nextStep(userDetails.id(), date, content));
    }
}
