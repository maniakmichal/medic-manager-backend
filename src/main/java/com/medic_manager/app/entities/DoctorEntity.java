package com.medic_manager.app.entities;

import com.medic_manager.app.enums.SpecializationEnum;
import jakarta.persistence.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Entity
@Table(name = "doctor")
@Validated
public class DoctorEntity extends UserEntity {

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "specialization", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<SpecializationEnum> specializationEnums;

    @Column(name = "imageUrl")
    private String imageUrl;

}
