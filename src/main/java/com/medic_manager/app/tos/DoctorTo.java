package com.medic_manager.app.tos;


import com.medic_manager.app.enums.SpecializationEnum;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DoctorTo(
        @Nullable Long id,
        @NotBlank String name,
        @NotBlank String surname,
        @NotBlank String email,
        @NotNull @NotEmpty List<SpecializationEnum> specializationEnums,
        @Nullable String imageUrl
) {
}
