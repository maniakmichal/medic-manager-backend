package com.medic_manager.app.common;

import java.time.OffsetDateTime;

public record ErrorResponseUtil(Throwable message, OffsetDateTime timestamp) {
}
