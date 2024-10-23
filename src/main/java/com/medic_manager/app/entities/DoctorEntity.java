package com.medic_manager.app.entities;

import com.medic_manager.app.enums.SpecializationEnum;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "doctor", uniqueConstraints = @UniqueConstraint(columnNames = "email"))
public class DoctorEntity extends UserEntity {

    @ElementCollection(fetch = FetchType.LAZY)
    @Column(name = "specialization", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<SpecializationEnum> specializationEnums;

    @Column(name = "imageUrl")
    private String imageUrl;

    public List<SpecializationEnum> getSpecializationEnums() {
        return specializationEnums;
    }

    public void setSpecializationEnums(List<SpecializationEnum> specializationEnums) {
        this.specializationEnums = specializationEnums;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
