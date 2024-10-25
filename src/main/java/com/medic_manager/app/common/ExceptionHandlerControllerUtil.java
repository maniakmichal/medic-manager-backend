package com.medic_manager.app.common;

import com.medic_manager.app.exceptions.AppointmentCreationFailedBusinessException;
import com.medic_manager.app.exceptions.IncorrectDayOfWeekBusinessException;
import com.medic_manager.app.exceptions.IncorrectHourOrMinutesBusinessException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
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
        return new ErrorResponseUtil(exception.getCause(), OffsetDateTime.now());
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponseUtil entityExistsException(EntityExistsException exception) {
        return new ErrorResponseUtil(exception.getCause(), OffsetDateTime.now());
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseUtil entityNotFoundException(EntityNotFoundException exception) {
        return new ErrorResponseUtil(exception.getCause(), OffsetDateTime.now());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseUtil illegalArgumentException(IllegalArgumentException exception) {
        return new ErrorResponseUtil(exception.getCause(), OffsetDateTime.now());
    }

    @ExceptionHandler(AppointmentCreationFailedBusinessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseUtil appointmentCreationFailedBusinessException(AppointmentCreationFailedBusinessException exception) {
        return new ErrorResponseUtil(exception.getCause(), OffsetDateTime.now());
    }

    @ExceptionHandler(IncorrectDayOfWeekBusinessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseUtil incorrectDayOfWeekBusinessException(IncorrectDayOfWeekBusinessException exception) {
        return new ErrorResponseUtil(exception.getCause(), OffsetDateTime.now());
    }

    @ExceptionHandler(IncorrectHourOrMinutesBusinessException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseUtil incorrectHourOrMinutesBusinessException(IncorrectHourOrMinutesBusinessException exception) {
        return new ErrorResponseUtil(exception.getCause(), OffsetDateTime.now());
    }
}
