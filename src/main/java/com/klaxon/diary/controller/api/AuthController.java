package com.klaxon.diary.controller.api;

import com.klaxon.diary.auth.JwtProvider;
import com.klaxon.diary.auth.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserService userService;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        userService.register(request.nickname, request.password);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.nickname, request.password)
        );
        String token = jwtProvider.generateToken(auth);
        return new JwtResponse(token);
    }

    public record AuthRequest(String nickname, String password) {}
    public record JwtResponse(String token) {}
}
