package com.medic_manager.app.tos;

import com.medic_manager.app.enums.GenderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record PatientTo(
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank String email,
        @NotNull LocalDate birthdate,
        @NotNull GenderEnum genderEnum
) {
}
