package com.klaxon.daily.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorRegistry {
    USER_ALREADY_EXISTS("User already exists"),
    USER_NOT_FOUND("User not found"),
    REFRESH_TOKEN_INVALID("Refresh token is invalid"),
    FORBIDDEN_CONTENT("Input content is invalid"),
    CONTENT_NEEDED("Input content is needed"),
    DIALOG_FINISHED("Dialog is finished")

    ;

    private final String message;
}
