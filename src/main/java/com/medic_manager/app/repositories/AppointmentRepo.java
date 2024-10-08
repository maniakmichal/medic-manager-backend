package com.medic_manager.app.repositories;

import com.medic_manager.app.entities.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepo extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findAllByPatientIdAndAppointmentDate(Long patientId, LocalDate appointmentDate);

    List<AppointmentEntity> findAllByDoctorIdAndAppointmentDate(Long doctorId, LocalDate appointmentDate);
}
