package com.klaxon.daily.error;

import com.klaxon.daily.config.log.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @Log
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse> appException(AppException e) {
        ErrorRegistry exceptionError = e.getError();
        HttpStatusCode statusCode = INTERNAL_SERVER_ERROR;
        String error = INTERNAL_SERVER_ERROR.name();
        String message = INTERNAL_SERVER_ERROR.getReasonPhrase();
        if (exceptionError != null) {
            error = exceptionError.name();
            message = exceptionError.getMessage();
        }
        log.error(e.getMessage(), e);
        return ResponseEntity.status(e.getHttpStatus() != null ? e.getHttpStatus() : statusCode).body(new ErrorResponse(error, message));
    }

    @Log
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> unauthorizedError(Throwable t) {
        log.error(t.getMessage(), t);
        return ResponseEntity.status(UNAUTHORIZED).body(new ErrorResponse(UNAUTHORIZED.name(), UNAUTHORIZED.getReasonPhrase()));
    }

    @Log
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        var error = ex.getBindingResult().getFieldErrors().stream().findFirst().get();
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(BAD_REQUEST.name(), error.getDefaultMessage()));
    }

    @Log
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            MissingServletRequestPartException.class,
            BindException.class
    })
    public ResponseEntity<ErrorResponse> badRequestError(Throwable t) {
        log.error(t.getMessage(), t);
        return ResponseEntity.status(BAD_REQUEST).body(new ErrorResponse(BAD_REQUEST.name(), t.getMessage()));
    }

    @Log
    @ExceptionHandler({
            NoHandlerFoundException.class,
            NoResourceFoundException.class
    })
    public ModelAndView handleNoHandlerFoundException() {
        return new ModelAndView("forward:/index.html");
    }


    @Log
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(Throwable t) {
        log.error(t.getMessage(), t);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ErrorResponse(INTERNAL_SERVER_ERROR.name(), t.getMessage()));
    }
}
