package com.medic_manager.app.repositories;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.testdata.AppointmentTestdata;
import com.medic_manager.app.testdata.DoctorTestdata;
import com.medic_manager.app.testdata.PatientTestdata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTestConfig
@DataJpaTest
class AppointmentRepoTest {
    
    private static final String EMAIL = "email@example.com";
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private PatientRepo patientRepo;

    @Test
    void findAllByPatientEntityAndAppointmentDate() {
        //given
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(EMAIL);
        DoctorEntity savedDoctor = doctorRepo.save(doctorEntity);
        PatientEntity patientEntity = PatientTestdata.mockPatientEntity(EMAIL);
        PatientEntity savedPatient = patientRepo.save(patientEntity);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(
                null,
                savedDoctor,
                savedPatient
        );
        AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
        //when
        List<AppointmentEntity> foundAppointments = appointmentRepo.findAllByPatientEntityAndAppointmentDate(savedPatient, savedAppointment.getAppointmentDate());
        //then
        assertThat(foundAppointments).hasSize(1);
        assertThat(foundAppointments.get(0)).usingRecursiveComparison().isEqualTo(savedAppointment);
    }

    @Test
    void findNoneByPatientEntityAndAppointmentDateWhenNoDateFound() {
        //given
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(EMAIL);
        DoctorEntity savedDoctor = doctorRepo.save(doctorEntity);
        PatientEntity patientEntity = PatientTestdata.mockPatientEntity(EMAIL);
        PatientEntity savedPatient = patientRepo.save(patientEntity);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(
                null,
                savedDoctor,
                savedPatient
        );
        appointmentRepo.save(appointmentEntity);
        //when
        List<AppointmentEntity> foundAppointments = appointmentRepo.findAllByPatientEntityAndAppointmentDate(savedPatient, LocalDate.MAX);
        //then
        assertThat(foundAppointments).isEmpty();
    }

    @Test
    void findNoneByPatientEntityAndAppointmentDateWhenNoPatientFound() {
        //given
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(EMAIL);
        DoctorEntity savedDoctor = doctorRepo.save(doctorEntity);
        PatientEntity patientEntity1 = PatientTestdata.mockPatientEntity(EMAIL);
        PatientEntity savedPatient1 = patientRepo.save(patientEntity1);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(
                null,
                savedDoctor,
                savedPatient1
        );
        AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
        PatientEntity patientEntity2 = PatientTestdata.mockPatientEntity("some_new_email@example.com");
        PatientEntity savedPatient2 = patientRepo.save(patientEntity2);
        //when
        List<AppointmentEntity> foundAppointments = appointmentRepo.findAllByPatientEntityAndAppointmentDate(savedPatient2, savedAppointment.getAppointmentDate());
        //then
        assertThat(foundAppointments).isEmpty();
    }

    @Test
    void findNoneByDoctorEntityAndAppointmentDateWhenNoDateFound() {
        //given
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(EMAIL);
        DoctorEntity savedDoctor = doctorRepo.save(doctorEntity);
        PatientEntity patientEntity = PatientTestdata.mockPatientEntity(EMAIL);
        PatientEntity savedPatient = patientRepo.save(patientEntity);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(
                null,
                savedDoctor,
                savedPatient
        );
        appointmentRepo.save(appointmentEntity);
        //when
        List<AppointmentEntity> foundAppointments = appointmentRepo.findAllByDoctorEntityAndAppointmentDate(savedDoctor, LocalDate.MAX);
        //then
        assertThat(foundAppointments).isEmpty();
    }

    @Test
    void findAllByDoctorEntityAndAppointmentDate() {
        //given
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(EMAIL);
        DoctorEntity savedDoctor = doctorRepo.save(doctorEntity);
        PatientEntity patientEntity = PatientTestdata.mockPatientEntity(EMAIL);
        PatientEntity savedPatient = patientRepo.save(patientEntity);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(
                null,
                savedDoctor,
                savedPatient
        );
        AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
        //when
        List<AppointmentEntity> foundAppointments = appointmentRepo.findAllByDoctorEntityAndAppointmentDate(savedDoctor, savedAppointment.getAppointmentDate());
        //then
        assertThat(foundAppointments).hasSize(1);
        assertThat(foundAppointments.get(0)).usingRecursiveComparison().isEqualTo(savedAppointment);
    }

    @Test
    void findNoneByDoctorEntityAndAppointmentDateWhenNoDoctorFound() {
        //given
        DoctorEntity doctorEntity1 = DoctorTestdata.mockDoctorEntity(EMAIL);
        DoctorEntity savedDoctor1 = doctorRepo.save(doctorEntity1);
        PatientEntity patientEntity = PatientTestdata.mockPatientEntity(EMAIL);
        PatientEntity savedPatient = patientRepo.save(patientEntity);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(
                null,
                savedDoctor1,
                savedPatient
        );
        AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
        DoctorEntity doctorEntity2 = DoctorTestdata.mockDoctorEntity("some_new_email@example.com");
        DoctorEntity savedDoctor2 = doctorRepo.save(doctorEntity2);
        //when
        List<AppointmentEntity> foundAppointments = appointmentRepo.findAllByDoctorEntityAndAppointmentDate(savedDoctor2, savedAppointment.getAppointmentDate());
        //then
        assertThat(foundAppointments).isEmpty();
    }
}
