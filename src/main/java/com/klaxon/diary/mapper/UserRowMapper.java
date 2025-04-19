package com.klaxon.diary.mapper;

import com.klaxon.diary.dto.AuthUser;
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
        return new AuthUser(
                UUID.fromString(rs.getString("id")),
                rs.getString("nickname"),
                rs.getString("password")
        );
    }
}