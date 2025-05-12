package com.klaxon.daily.repository;

import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.dto.AuthUser;
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
    private final RowMapper<AuthUser> userRowMapper;

    @Log
    public AuthUser save(AuthUser authUser) {
        var sql = """
                INSERT INTO "user".user (id, nickname, password)
                VALUES (:id, :nickname, :password)
                RETURNING id, nickname, password
                """;
        return jdbcTemplate.queryForObject(sql,
                Map.of("id", authUser.id(), "nickname", authUser.nickname(), "password", authUser.password()),
                userRowMapper);
    }

    @Log
    public Optional<AuthUser> findByNickname(String nickname) {
        var sql = """
                SELECT id,
                       nickname,
                       password
                FROM "user".user
                WHERE nickname = :nickname
                  AND NOT deleted
                """;
        return Optional.ofNullable(singleResult(
                jdbcTemplate.query(sql, Map.of("nickname", nickname), userRowMapper)
        ));
    }

    @Log
    public Optional<AuthUser> findById(UUID id) {
        var sql = """
                SELECT id,
                       nickname,
                       password
                FROM "user".user
                WHERE id = :id
                  AND NOT deleted
                """;
        return Optional.ofNullable(singleResult(
                jdbcTemplate.query(sql, Map.of("id", id), userRowMapper)
        ));
    }
}
