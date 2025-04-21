package com.klaxon.diary.controller.api;

import com.klaxon.diary.config.log.Log;
import com.klaxon.diary.dto.AuthUser;
import com.klaxon.diary.error.AppException;
import com.klaxon.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.klaxon.diary.error.ErrorRegistry.USER_NOT_FOUND;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final UserRepository userRepository;

    @Log
    @GetMapping("/{nickname}")
    public ResponseEntity<AuthUser> getDevices(@PathVariable String nickname) {
        var list = userRepository.findByNickname(nickname).orElseThrow(() -> AppException.builder().error(USER_NOT_FOUND).build());
        return ResponseEntity.ok().body(list);
    }

}
