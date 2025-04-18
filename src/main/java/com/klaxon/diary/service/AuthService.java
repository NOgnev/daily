package com.klaxon.diary.service;

import com.klaxon.diary.config.security.JwtProvider;
import com.klaxon.diary.dto.AuthRequest;
import com.klaxon.diary.dto.JwtResponse;
import com.klaxon.diary.dto.User;
import com.klaxon.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    public User register(String nickname, String password) {
        if (userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("User with nickname " + nickname + " already exists");
        }

        return userRepository.save(new User(UUID.randomUUID(), nickname, passwordEncoder.encode(password)));
    }

    public JwtResponse login(AuthRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.nickname(), request.password())
        );

        String accessToken = jwtProvider.generateAccessToken(auth);
        String refreshToken = jwtProvider.generateRefreshToken(auth);

        return new JwtResponse(accessToken, refreshToken);
    }

    public JwtResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken) || !jwtProvider.isRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        var username = jwtProvider.getUsernameFromToken(refreshToken);
        var userDetails = userDetailsService.loadUserByUsername(username);
        var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        var newAccessToken = jwtProvider.generateAccessToken(authToken);

        return new JwtResponse(newAccessToken, refreshToken);
    }
}
