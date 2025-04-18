package com.klaxon.diary.service;

import com.klaxon.diary.dto.User;
import com.klaxon.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User register(String nickname, String password) {
        if (userRepository.existsByNickname(nickname)) {
            throw new RuntimeException("User with nickname " + nickname + " already exists");
        }
        return userRepository.save(
                new User(UUID.randomUUID(), nickname, passwordEncoder.encode(password))
        );
    }
}
