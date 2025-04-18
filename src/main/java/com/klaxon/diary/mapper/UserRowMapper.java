package com.klaxon.diary.mapper;

import com.klaxon.diary.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UserRowMapper implements RowMapper<User> {


    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                UUID.fromString(rs.getString("id")),
                rs.getString("nickname"),
                rs.getString("password")
        );
    }
}