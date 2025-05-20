package com.klaxon.daily.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.daily.config.log.Log;
import com.klaxon.daily.dto.Dialog;
import com.klaxon.daily.dto.DialogItem;
import com.klaxon.daily.dto.Summary;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
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
    private final RowMapper<Dialog> dialogRowMapper;

    @Log
    public List<DialogItem> findMessagesByUserIdAndDate(UUID userId, LocalDate date) {
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
    public Dialog findByUserIdAndDate(UUID userId, LocalDate date) {
        var sql = """
                SELECT user_id, date, messages, status
                FROM daily.dialog
                WHERE user_id = :userId
                  AND date = :date
                """;
        return jdbcTemplate.queryForObject(sql, Map.of("userId", userId, "date", date), dialogRowMapper);
    }

    @Log
    public List<Summary> getSummaries(UUID userId, LocalDate date) {
        var sql = """
                SELECT date, summary
                FROM daily.dialog
                WHERE user_id = :userId
                  AND date >= :date - interval '5 days'
                  AND date < :date
                ORDER BY date;
                """;
        return jdbcTemplate.query(sql, Map.of("userId", userId, "date", date), (rs, rowNum) ->
                Summary.builder()
                        .date(rs.getObject("date", LocalDate.class))
                        .summary(rs.getString("summary"))
                        .build()
        );
    }

    @Log
    public List<DialogItem> addDialogItem(UUID userId, LocalDate date, DialogItem item, Dialog.Status status, String summary) {
        var sql = """
                INSERT INTO daily.dialog (
                    user_id, date, status, summary, messages
                )
                VALUES (
                    :userId,
                    :date,
                    :status::daily.dialog_status,
                    :summary,
                    to_jsonb(
                        array[
                            json_build_object(
                                'id', :itemId,
                                'role', :role,
                                'type', :type,
                                'content', :content
                            )
                        ]
                    )::jsonb
                )
                ON CONFLICT (user_id, date) DO UPDATE
                SET status = :status::daily.dialog_status,
                    summary = :summary,
                    messages = daily.dialog.messages || to_jsonb(
                        json_build_object(
                            'id', :itemId,
                            'role', :role,
                            'type', :type,
                            'content', :content
                        )
                    )::jsonb
                WHERE daily.dialog.user_id = :userId
                  AND daily.dialog.date = :date
                RETURNING messages;
                """;
        var params = new MapSqlParameterSource()
                .addValue("userId", userId)
                .addValue("date", date)
                .addValue("status", status.name().toLowerCase())
                .addValue("summary", summary)
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
