package com.klaxon.diary.controller.api;

import com.klaxon.diary.config.log.Log;
import com.klaxon.diary.dto.AuthUser;
import com.klaxon.diary.dto.response.LoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    @Log
    @GetMapping
    public ResponseEntity<LoginResponse.User> getCurrentUser(@AuthenticationPrincipal AuthUser userDetails) {
        return ResponseEntity.ok().body(LoginResponse.User.builder()
                .id(userDetails.id())
                .nickname(userDetails.nickname())
                .build());
    }
}
