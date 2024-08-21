package com.medic_manager.app.common;

import java.time.OffsetDateTime;

public record ErrorResponseUtil(String message, OffsetDateTime timestamp) {
}
