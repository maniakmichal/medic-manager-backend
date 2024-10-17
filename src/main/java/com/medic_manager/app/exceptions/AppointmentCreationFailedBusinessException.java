package com.medic_manager.app.exceptions;

public class AppointmentCreationFailedBusinessException extends RuntimeException {

    public AppointmentCreationFailedBusinessException(String message) {
        super(message);
    }
}
