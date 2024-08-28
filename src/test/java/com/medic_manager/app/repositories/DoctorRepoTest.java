package com.medic_manager.app.repositories;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.DoctorEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static com.medic_manager.app.testdata.DoctorTestdata.mockDoctorEntityWithEmail;
import static org.assertj.core.api.Assertions.assertThat;

@UnitTestConfig
@DataJpaTest
class DoctorRepoTest {

    private static final String EMAIL = "email@example.com";
    @Autowired
    private DoctorRepo doctorRepo;

    @Test
    void findByEmailIgnoreCase() {
        //given
        DoctorEntity doctor = mockDoctorEntityWithEmail(EMAIL);
        DoctorEntity savedDoctor = doctorRepo.save(doctor);
        //when
        Optional<DoctorEntity> foundDoctor = doctorRepo.findByEmailIgnoreCase("EmAiL@eXaMpLe.CoM");
        //then
        assertThat(foundDoctor).isPresent();
        assertThat(foundDoctor.get()).usingRecursiveComparison().isEqualTo(savedDoctor);
    }

    @Test
    void notFindByEmailIgnoreCaseWhenNotExistingEmail() {
        //given
        DoctorEntity doctor = mockDoctorEntityWithEmail(EMAIL);
        doctorRepo.save(doctor);
        //when
        Optional<DoctorEntity> foundDoctor = doctorRepo.findByEmailIgnoreCase("NOT_EXISTING_EMAIL");
        //then
        assertThat(foundDoctor).isNotPresent();
    }

    @Test
    void notFindByEmailIgnoreCaseWhenSearchByNotFullLengthEmail() {
        //given
        DoctorEntity doctor = mockDoctorEntityWithEmail(EMAIL);
        doctorRepo.save(doctor);
        //when
        Optional<DoctorEntity> foundDoctor = doctorRepo.findByEmailIgnoreCase("email@example.co");
        //then
        assertThat(foundDoctor).isNotPresent();
    }
}
