package com.klaxon.diary.mapper;

import com.klaxon.diary.dto.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RefreshTokenRowMapper implements RowMapper<RefreshToken> {

    @Override
    public RefreshToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        return RefreshToken.builder()
                .userId(UUID.fromString(rs.getString("user_id")))
                .token(rs.getString("token"))
                .device(RefreshToken.Device.builder()
                        .id(UUID.fromString(rs.getString("device_id")))
                        .expiryDate(rs.getTimestamp("expiry_date").toInstant())
                        .build())
                .build();
    }
}