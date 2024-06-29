package com.medic_manager.app.entities;

import com.medic_manager.app.enums.SpecializationEnum;
import jakarta.persistence.*;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "doctor")
@Validated
public class DoctorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime created_at;

    @Column(name = "modified_at", nullable = false)
    private LocalDateTime modified_at;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "surname", nullable = false)
    private String surname;

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "specialization", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<SpecializationEnum> specializationEnums;

    @Column(name = "imageUrl")
    private String imageUrl;

    @Column(name = "email", nullable = false)
    private String email;
}
