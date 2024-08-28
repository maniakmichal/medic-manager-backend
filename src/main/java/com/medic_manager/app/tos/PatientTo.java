package com.medic_manager.app.tos;

import com.medic_manager.app.enums.GenderEnum;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PatientTo(
        @Nullable Long id,
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank String email,
        @NotNull LocalDate birthdate,
        @NotNull GenderEnum genderEnum
) {
}
