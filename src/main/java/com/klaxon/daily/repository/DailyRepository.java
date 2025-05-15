package com.klaxon.daily.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.dto.DialogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DailyRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Log
    public List<DialogItem> findAllByUserIdAndDate(UUID userId, LocalDate date) {
        var sql = """
                SELECT messages
                FROM daily.dialog
                WHERE user_id = :userId
                  AND date = :date
                """;
        return jdbcTemplate.queryForObject(sql, Map.of("userId", userId, "date", date), (rs, rowNum) -> {
            String json = rs.getString("messages");
            try {
                return objectMapper.readValue(
                        json,
                        new TypeReference<>() {}
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse DialogItem list from JSON", e);
            }
        });
    }

    @Log
    public List<DialogItem> addDialogItem(UUID userId, LocalDate date, DialogItem item) {
        var sql = """
                UPDATE daily.dialog
                SET messages = messages || to_jsonb(
                  json_build_object(
                    'id', :itemId,
                    'role', :role,
                    'type', :type,
                    'content', :content
                  )
                )::jsonb
                WHERE user_id = :userId
                  AND date = :date
                RETURNING messages
                """;
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("date", date)
                .addValue("itemId", item.id())
                .addValue("role", item.role().name().toLowerCase())
                .addValue("type", item.type().name().toLowerCase())
                .addValue("content", item.content());

        return jdbcTemplate.queryForObject(sql, params, (rs, rowNum) -> {
            String json = rs.getString("messages");
            try {
                return objectMapper.readValue(
                        json,
                        new TypeReference<>() {}
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Failed to parse DialogItem list from JSON", e);
            }
        });
    }
}
