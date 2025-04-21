package com.klaxon.diary.config.security;

import com.klaxon.diary.error.AppException;
import com.klaxon.diary.error.ErrorRegistry;
import com.klaxon.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String nickname) throws UsernameNotFoundException {
        return userRepository.findByNickname(nickname)
                .orElseThrow(() -> AppException.builder()
                        .httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
                        .error(ErrorRegistry.USER_NOT_FOUND)
                        .args(Map.of("nickname", nickname))
                        .build());
    }
}
