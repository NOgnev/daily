package com.klaxon.daily.mapper;

import com.klaxon.daily.dto.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<AuthUser> {

    @Override
    public AuthUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        return AuthUser.builder()
                .id(UUID.fromString(rs.getString("id")))
                .nickname(rs.getString("nickname"))
                .password(rs.getString("password"))
                .build();
    }
}