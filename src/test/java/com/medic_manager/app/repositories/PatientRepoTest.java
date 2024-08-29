package com.medic_manager.app.repositories;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.PatientEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static com.medic_manager.app.testdata.PatientTestdata.mockPatientEntity;
import static org.assertj.core.api.Assertions.assertThat;

@UnitTestConfig
@DataJpaTest
class PatientRepoTest {

    private static final String EMAIL = "email@example.com";
    @Autowired
    private PatientRepo patientRepo;

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
}
