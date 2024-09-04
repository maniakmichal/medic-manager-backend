package com.medic_manager.app.entities;

import com.medic_manager.app.enums.AppointmentStatusEnum;
import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Entity
@Table(name = "appointment")
public class AppointmentEntity extends BaseEntity {

    @Column(name = "appointmentDate", nullable = false)
    private LocalDate appointmentDate;

    @Column(name = "appointmentDayOfWeek", nullable = false)
    @Enumerated(EnumType.STRING)
    private DayOfWeek appointmentDayOfWeek;

    @Column(name = "appointmentHour", nullable = false)
    private byte appointmentHour;

    @Column(name = "appointmentMinute", nullable = false)
    private byte appointmentMinute;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatusEnum appointmentStatusEnum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor", nullable = false)
    private DoctorEntity doctorEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient", nullable = false)
    private PatientEntity patientEntity;

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public DayOfWeek getAppointmentDayOfWeek() {
        return appointmentDayOfWeek;
    }

    public void setAppointmentDayOfWeek(DayOfWeek appointmentDayOfWeek) {
        this.appointmentDayOfWeek = appointmentDayOfWeek;
    }

    public byte getAppointmentHour() {
        return appointmentHour;
    }

    public void setAppointmentHour(byte appointmentHour) {
        this.appointmentHour = appointmentHour;
    }

    public byte getAppointmentMinute() {
        return appointmentMinute;
    }

    public void setAppointmentMinute(byte appointmentMinute) {
        this.appointmentMinute = appointmentMinute;
    }

    public AppointmentStatusEnum getAppointmentStatusEnum() {
        return appointmentStatusEnum;
    }

    public void setAppointmentStatusEnum(AppointmentStatusEnum appointmentStatusEnum) {
        this.appointmentStatusEnum = appointmentStatusEnum;
    }

    public DoctorEntity getDoctorEntity() {
        return doctorEntity;
    }

    public void setDoctorEntity(DoctorEntity doctorEntity) {
        this.doctorEntity = doctorEntity;
    }

    public PatientEntity getPatientEntity() {
        return patientEntity;
    }

    public void setPatientEntity(PatientEntity patientEntity) {
        this.patientEntity = patientEntity;
    }
}
