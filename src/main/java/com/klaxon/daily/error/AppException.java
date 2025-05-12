package com.klaxon.daily.error;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter
@Builder
public class AppException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final ErrorRegistry error;
    private final Map<String, String> args;

    @Override
    public String getMessage() {
        return error.getMessage();
    }
}
