package com.klaxon.diary.config.security;

import com.klaxon.diary.dto.User;
import com.klaxon.diary.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("User with nickname " + username + " not found"));
        return org.springframework.security.core.userdetails.User
                .withUsername(user.nickname())
                .password(user.password())
                .authorities("USER")
                .build();
    }
}
