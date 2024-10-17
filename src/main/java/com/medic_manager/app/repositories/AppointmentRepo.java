package com.medic_manager.app.repositories;

import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface AppointmentRepo extends JpaRepository<AppointmentEntity, Long> {
    List<AppointmentEntity> findAllByPatientEntityAndAppointmentDate(PatientEntity patientEntity, LocalDate appointmentDate);

    List<AppointmentEntity> findAllByDoctorEntityAndAppointmentDate(DoctorEntity doctorEntity, LocalDate appointmentDate);
}
