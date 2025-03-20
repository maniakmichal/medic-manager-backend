package com.medic_manager.app.repositories;

import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.testdata.AppointmentTestdata;
import com.medic_manager.app.testdata.DoctorTestdata;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.medic_manager.app.testdata.PatientTestdata.mockPatientEntity;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class PatientRepoTest {

    private static final String EMAIL = "email@example.com";
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private DoctorRepo doctorRepo;
    @Autowired
    private AppointmentRepo appointmentRepo;

    @Test
    void findByEmailIgnoreCase() {
        //given
        PatientEntity patient = mockPatientEntity(EMAIL);
        PatientEntity savedPatient = patientRepo.save(patient);
        //when
        Optional<PatientEntity> foundPatient = patientRepo.findByEmailIgnoreCase("EmAiL@eXaMpLe.CoM");
        //then
        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get()).usingRecursiveComparison().isEqualTo(savedPatient);
    }

    @Test
    void notFindByEmailIgnoreCaseWhenNotExistingEmail() {
        //given
        PatientEntity patient = mockPatientEntity(EMAIL);
        patientRepo.save(patient);
        //when
        Optional<PatientEntity> foundPatient = patientRepo.findByEmailIgnoreCase("NOT_EXISTING_EMAIL");
        //then
        assertThat(foundPatient).isNotPresent();
    }

    @Test
    void notFindByEmailIgnoreCaseWhenSearchByNotFullLengthEmail() {
        //given
        PatientEntity patient = mockPatientEntity(EMAIL);
        patientRepo.save(patient);
        //when
        Optional<PatientEntity> foundPatient = patientRepo.findByEmailIgnoreCase("email@example.co");
        //then
        assertThat(foundPatient).isNotPresent();
    }

    @Test
    void findByIdWithAppointments() {
        //given
        PatientEntity patient = mockPatientEntity(EMAIL);
        PatientEntity savedPatient = patientRepo.save(patient);
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(EMAIL);
        DoctorEntity savedDoctor = doctorRepo.save(doctorEntity);
        AppointmentEntity appointment = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor, savedPatient);
        AppointmentEntity savedAppointment = appointmentRepo.save(appointment);
        savedPatient.setAppointmentEntityList(new ArrayList<>(List.of(savedAppointment)));
        patientRepo.save(savedPatient);
        //when
        Optional<PatientEntity> foundPatient = patientRepo.findByIdWithAppointments(savedPatient.getId());
        //then
        assertThat(foundPatient).isPresent();
        assertThat(foundPatient.get()).usingRecursiveComparison().isEqualTo(savedPatient);
        assertThat(foundPatient.get().getAppointmentEntityList()).isNotEmpty().hasSize(1);
        assertThat(foundPatient.get().getAppointmentEntityList().get(0))
                .usingRecursiveComparison()
                .isEqualTo(savedAppointment);
    }

    @Test
    void findNoneByIdWithAppointmentsWhenIdNotPresent() {
        //given
        PatientEntity patient = mockPatientEntity(EMAIL);
        patientRepo.save(patient);
        //when
        Optional<PatientEntity> foundPatient = patientRepo.findByIdWithAppointments(Long.MAX_VALUE);
        //then
        assertThat(foundPatient).isNotPresent();
    }
}
