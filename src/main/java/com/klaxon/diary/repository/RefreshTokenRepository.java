package com.klaxon.diary.repository;

import com.klaxon.diary.config.log.Log;
import com.klaxon.diary.config.log.hidden.Hidden;
import com.klaxon.diary.dto.RefreshToken;
import com.klaxon.diary.mapper.RefreshTokenRowMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.dao.support.DataAccessUtils.singleResult;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final RefreshTokenRowMapper refreshTokenRowMapper;

    @Log
    public List<RefreshToken> findAllByUserId(UUID userId) {
        var sql = """
                SELECT user_id,
                       token,
                       device_id,
                       expiry_date
                FROM diary.refresh_token
                WHERE user_id = :userId
                """;
        return jdbcTemplate.query(sql, Map.of("userId", userId), refreshTokenRowMapper);
    }

    @Log
    public List<RefreshToken> findAllByToken(@Hidden String token) {
        var sql = """
                WITH owner_user AS (
                    SELECT user_id
                    FROM diary.refresh_token
                    WHERE token = :token
                )
                SELECT rt.user_id,
                       rt.token,
                       rt.device_id,
                       rt.expiry_date
                FROM diary.refresh_token rt
                JOIN owner_user ou ON rt.user_id = ou.user_id;
                """;
        return jdbcTemplate.query(sql, Map.of("token", token), refreshTokenRowMapper);
    }

    @Log
    public Optional<RefreshToken> find(@Hidden String token) {
        var sql = """
                SELECT user_id,
                       token,
                       device_id,
                       expiry_date
                FROM diary.refresh_token
                WHERE token = :token
                """;
        return Optional.ofNullable(singleResult(
                jdbcTemplate.query(sql, Map.of("token", token), refreshTokenRowMapper)
        ));
    }

    @Log
    public RefreshToken save(RefreshToken token) {
        var sql = """
                INSERT INTO diary.refresh_token (user_id, token, device_id, expiry_date)
                VALUES (:userId, :token, :deviceId, :expiryDate)
                RETURNING user_id, token, device_id, expiry_date
                """;
        return jdbcTemplate.queryForObject(sql,
                Map.of("userId", token.userId(),
                        "token", token.token(),
                        "deviceId", token.device().id(),
                        "expiryDate", Timestamp.from(token.device().expiryDate())),
                refreshTokenRowMapper);
    }

    @Log
    public void delete(UUID userId, UUID deviceId) {
        var sql = """
                DELETE FROM diary.refresh_token
                WHERE user_id = :userId
                  AND device_id = :deviceId
                """;
        jdbcTemplate.update(sql, Map.of("userId", userId, "deviceId", deviceId));
    }

    @Log
    public void delete(String token) {
        var sql = """
                DELETE FROM diary.refresh_token
                WHERE token = :token
                """;
        jdbcTemplate.update(sql, Map.of("token", token));
    }

    @Log
    public void deleteExpired() {
        var sql = """
                DELETE FROM diary.refresh_token
                WHERE expiry_date < now()
                """;
        jdbcTemplate.update(sql, Map.of());
    }
}
