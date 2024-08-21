package com.medic_manager.app.common;

import jakarta.persistence.EntityExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

@RestControllerAdvice
public class ExceptionHandlerControllerUtil {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseUtil methodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return new ErrorResponseUtil(exception.getLocalizedMessage(), OffsetDateTime.now());
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseUtil entityExistsException(EntityExistsException exception) {
        return new ErrorResponseUtil(exception.getLocalizedMessage(), OffsetDateTime.now());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseUtil illegalArgumentException(IllegalArgumentException exception) {
        return new ErrorResponseUtil(exception.getLocalizedMessage(), OffsetDateTime.now());
    }
}
