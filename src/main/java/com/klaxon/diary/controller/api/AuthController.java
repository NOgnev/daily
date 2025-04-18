package com.klaxon.diary.controller.api;

import com.klaxon.diary.config.security.JwtProvider;
import com.klaxon.diary.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final AuthService authService;
    private final UserDetailsService userDetailsService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        authService.register(request.nickname, request.password);
        return ResponseEntity.ok().body("User registered successfully");
    }

    @PostMapping("/login")
    public JwtResponse login(@RequestBody AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.nickname, request.password)
        );
        String accessToken = jwtProvider.generateAccessToken(auth);
        String refreshToken = jwtProvider.generateRefreshToken(auth);
        return new JwtResponse(accessToken, refreshToken);
    }

    @PostMapping("/refresh")
    public JwtResponse refresh(@RequestBody Map<String, String> request) {
        var refreshToken = request.get("refreshToken");
        if (!jwtProvider.validateToken(refreshToken) || !jwtProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }
        var username = jwtProvider.getUsernameFromToken(refreshToken);
        var userDetails = userDetailsService.loadUserByUsername(username);
        var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        var newAccessToken = jwtProvider.generateAccessToken(authToken);
        return new JwtResponse(newAccessToken, refreshToken);
    }

    public record AuthRequest(String nickname, String password) {}
    public record JwtResponse(String accessToken, String refreshToken) {}
}
