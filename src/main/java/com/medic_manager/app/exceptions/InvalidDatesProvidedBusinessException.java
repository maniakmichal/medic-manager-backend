package com.medic_manager.app.exceptions;

public class InvalidDatesProvidedBusinessException extends RuntimeException {
    public InvalidDatesProvidedBusinessException(String message) {
        super(message);
    }
}
