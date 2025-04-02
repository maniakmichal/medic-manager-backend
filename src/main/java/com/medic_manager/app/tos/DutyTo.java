package com.medic_manager.app.tos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record DutyTo(
        @Nullable Long id,
        @NotNull Long doctorId,
        @NotNull LocalDate startDate,
        @NotNull LocalDate endDate
) {
}
