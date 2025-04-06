package com.medic_manager.app.repositories;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.DutyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static com.medic_manager.app.testdata.DoctorTestdata.mockDoctorEntity;
import static com.medic_manager.app.testdata.DutyTestdata.mockDutyEntity;
import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@DataJpaTest
class DutyRepoTest {

    @Autowired
    private DutyRepo dutyRepo;
    @Autowired
    private DoctorRepo doctorRepo;

    private DoctorEntity savedDoctor;

    @BeforeEach
    void setUp() {
        dutyRepo.deleteAll();
        doctorRepo.deleteAll();
        LocalDate startDate = LocalDate.of(2021, 7, 10);
        LocalDate endDate = LocalDate.of(2021, 7, 13);
        DoctorEntity doctorEntity = mockDoctorEntity();
        savedDoctor = doctorRepo.save(doctorEntity);
        DutyEntity dutyEntity = mockDutyEntity(
                null,
                savedDoctor,
                startDate,
                endDate
        );
        dutyRepo.save(dutyEntity);
    }

    @Test
    void countOneDutyWhenStartDateBetween() {
        //given
        LocalDate startDate = LocalDate.of(2021, 7, 13);
        LocalDate endDate = LocalDate.of(2021, 7, 18);
        //when
        int dutiesCount = dutyRepo.countDutyByDoctorIdAndDatesBetween(
                savedDoctor.getId(),
                startDate,
                endDate
        );
        //then
        assertThat(dutiesCount).isNotZero().isEqualTo(1);
    }

    @Test
    void countOneDutyWhenEndDateBetween() {
        //given
        LocalDate startDate = LocalDate.of(2021, 7, 5);
        LocalDate endDate = LocalDate.of(2021, 7, 10);
        //when
        int dutiesCount = dutyRepo.countDutyByDoctorIdAndDatesBetween(
                savedDoctor.getId(),
                startDate,
                endDate
        );
        //then
        assertThat(dutiesCount).isNotZero().isEqualTo(1);
    }

    @Test
    void countOneDutyWhenStartAndEndDateCoverOtherDuty() {
        //given
        LocalDate startDate = LocalDate.of(2021, 7, 7);
        LocalDate endDate = LocalDate.of(2021, 7, 17);
        //when
        int dutiesCount = dutyRepo.countDutyByDoctorIdAndDatesBetween(
                savedDoctor.getId(),
                startDate,
                endDate
        );
        //then
        assertThat(dutiesCount).isNotZero().isEqualTo(1);
    }

    @Test
    void countZeroWhenNoDoctorIdFound() {
        //given
        LocalDate startDate = LocalDate.of(2021, 7, 9);
        LocalDate endDate = LocalDate.of(2021, 7, 21);
        //when
        int dutiesCount = dutyRepo.countDutyByDoctorIdAndDatesBetween(
                Long.MAX_VALUE,
                startDate,
                endDate
        );
        //then
        assertThat(dutiesCount).isZero();
    }

    @Test
    void countZeroWhenNoDatesBetween() {
        //given
        LocalDate startDate = LocalDate.of(2021, 7, 14);
        LocalDate endDate = LocalDate.of(2021, 7, 18);
        //when
        int dutiesCount = dutyRepo.countDutyByDoctorIdAndDatesBetween(
                Long.MAX_VALUE,
                startDate,
                endDate
        );
        //then
        assertThat(dutiesCount).isZero();
    }
}
