package com.klaxon.diary.error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(Exception e) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error", e.getMessage()));
    }
}
