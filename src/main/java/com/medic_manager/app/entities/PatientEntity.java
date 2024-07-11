package com.medic_manager.app.entities;

import com.medic_manager.app.enums.GenderEnum;
import jakarta.persistence.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Entity
@Table(name = "patient")
@Validated
public class PatientEntity extends UserEntity {

    @Column(name = "birthdate", nullable = false)
    private LocalDate birthdate;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "gender", nullable = false)
    @Enumerated(EnumType.STRING)
    private GenderEnum genderEnum;

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public GenderEnum getGenderEnum() {
        return genderEnum;
    }
}
