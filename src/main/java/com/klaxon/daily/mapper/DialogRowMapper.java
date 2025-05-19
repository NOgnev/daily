package com.klaxon.daily.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.klaxon.daily.dto.Dialog;
import com.klaxon.daily.dto.DialogItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DialogRowMapper implements RowMapper<Dialog> {

    private final ObjectMapper objectMapper;

    @Override
    public Dialog mapRow(ResultSet rs, int rowNum) throws SQLException {
        List<DialogItem> messages;
        try {
            messages = objectMapper.readValue(rs.getString("messages"), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse DialogItem list from JSON", e);
        }
        return Dialog.builder()
                .userId(UUID.fromString(rs.getString("user_id")))
                .date(rs.getObject("date", LocalDate.class))
                .status(Dialog.Status.fromString(rs.getString("status")))
                .messages(messages)
                .build();
    }
}