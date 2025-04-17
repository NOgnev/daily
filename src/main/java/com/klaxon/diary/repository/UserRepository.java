package com.klaxon.diary.repository;

import com.klaxon.diary.dto.User;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class UserRepository {
    public static Set<User> NICKNAMES = new HashSet<>();

    public boolean existsByNickname(String nickname) {
        return NICKNAMES.stream().anyMatch(u -> u.nickname().equals(nickname));
    }

    public User save(User user) {
        NICKNAMES.add(user);
        return user;
    }

    public Optional<User> findByNickname(String nickname) {
        return NICKNAMES.stream()
                .filter(u -> u.nickname().equals(nickname))
                .findFirst();
    }
}
