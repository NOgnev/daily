package com.klaxon.diary.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Hello {

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, World!";
    }

    @GetMapping("/user")
    public String getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return "Current user: " + userDetails.getUsername();
    }
}
