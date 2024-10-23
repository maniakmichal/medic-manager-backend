package com.medic_manager.app.tos;

import com.medic_manager.app.enums.AppointmentStatusEnum;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalDate;

public record AppointmentTo(
        @Nullable Long id,
        @NotNull LocalDate appointmentDate,
        @NotNull DayOfWeek appointmentDayOfWeek,
        byte appointmentHour,
        byte appointmentMinute,
        @NotNull AppointmentStatusEnum appointmentStatusEnum,
        @NotNull Long doctorId,
        @NotNull Long patientId
) {
}
