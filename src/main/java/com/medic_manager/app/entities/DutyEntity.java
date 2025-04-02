package com.medic_manager.app.entities;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "duty")
public class DutyEntity extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "doctor", nullable = false)
    private DoctorEntity doctorEntity;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    public DoctorEntity getDoctorEntity() {
        return doctorEntity;
    }

    public void setDoctorEntity(DoctorEntity doctorEntity) {
        this.doctorEntity = doctorEntity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
