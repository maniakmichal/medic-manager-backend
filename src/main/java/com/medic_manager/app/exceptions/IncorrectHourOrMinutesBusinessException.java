package com.medic_manager.app.exceptions;

public class IncorrectHourOrMinutesBusinessException extends RuntimeException {

    public IncorrectHourOrMinutesBusinessException(String message) {
        super(message);
    }
}
