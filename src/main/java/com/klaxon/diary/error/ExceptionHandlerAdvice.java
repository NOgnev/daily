package com.klaxon.diary.error;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> appException(AppException e) {
        ErrorRegistry exceptionError = e.getError();
        String error = INTERNAL_SERVER_ERROR.name();
        String message = INTERNAL_SERVER_ERROR.getReasonPhrase();
        if (exceptionError != null) {
            error = exceptionError.name();
            message = exceptionError.getMessage();
        }
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(error, message));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> unauthorizedError(Throwable t) {
        return ResponseEntity.status(UNAUTHORIZED).body(new ErrorResponse(UNAUTHORIZED.name(), t.getMessage()));
    }

    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            MissingServletRequestPartException.class,
            BindException.class
    })
    public ResponseEntity<ErrorResponse> badRequestError(Throwable t) {
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(BAD_REQUEST.name(), t.getMessage()));
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(Throwable t) {
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ErrorResponse(INTERNAL_SERVER_ERROR.name(), t.getMessage()));
    }
}
