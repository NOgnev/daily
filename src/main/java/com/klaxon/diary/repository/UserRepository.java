package com.klaxon.diary.repository;

import com.klaxon.diary.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.dao.support.DataAccessUtils.singleResult;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RowMapper<User> userRowMapper;


    public User save(User user) {
        var sql = """
                INSERT INTO diary.user (id, nickname, password)
                VALUES (:id, :nickname, :password)
                RETURNING id, nickname, password
                """;
        return jdbcTemplate.queryForObject(sql,
                Map.of("id", user.id(), "nickname", user.nickname(), "password", user.password()),
                userRowMapper);
    }

    public Optional<User> findByNickname(String nickname) {
        var sql = """
                SELECT id,
                       nickname,
                       password
                FROM diary.user
                WHERE nickname = :nickname
                  AND NOT deleted
                """;
        return Optional.ofNullable(singleResult(
                jdbcTemplate.query(sql, Map.of("nickname", nickname), userRowMapper)
        ));
    }

    public Optional<User> findById(UUID id) {
        var sql = """
                SELECT id,
                       nickname,
                       password
                FROM diary.user
                WHERE id = :id
                  AND NOT deleted
                """;
        return Optional.ofNullable(singleResult(
                jdbcTemplate.query(sql, Map.of("id", id), userRowMapper)
        ));
    }
}
