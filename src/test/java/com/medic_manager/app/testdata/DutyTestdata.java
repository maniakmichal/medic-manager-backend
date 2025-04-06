package com.medic_manager.app.testdata;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.DutyEntity;
import com.medic_manager.app.tos.DutyTo;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;
import java.util.stream.Stream;

@TestComponent
public class DutyTestdata {

    private static final LocalDate START_DATE = LocalDate.of(2023, 9, 27);
    private static final LocalDate END_DATE = LocalDate.of(2023, 10, 10);
    private static final Long ID = 1L;

    public static DutyEntity mockDutyEntity() {
        return mockDutyEntity(null, DoctorTestdata.mockDoctorEntity());
    }

    public static DutyEntity mockDutyEntity(Long id, DoctorEntity doctorEntity) {
        DutyEntity dutyEntity = new DutyEntity();
        dutyEntity.setId(id);
        dutyEntity.setStartDate(START_DATE);
        dutyEntity.setEndDate(END_DATE);
        dutyEntity.setDoctorEntity(doctorEntity);
        return dutyEntity;
    }

    public static DutyTo mockDutyTo() {
        return mockDutyTo(null);
    }

    public static DutyTo mockDutyTo(Long id) {
        return mockDutyTo(id, ID);
    }

    public static DutyTo mockDutyTo(Long id, Long doctorId) {
        return mockDutyTo(
                id,
                doctorId,
                START_DATE,
                END_DATE
        );
    }

    public static DutyTo mockDutyTo(Long id, Long doctorId, LocalDate startDate, LocalDate endDate) {
        return new DutyTo(
                id,
                doctorId,
                startDate,
                endDate
        );
    }

    public static Stream<Arguments> provideInvalidCreateDutyToList() {
        return Stream.of(
                null,
                Arguments.of(new DutyTo(ID, ID, START_DATE, END_DATE)),
                Arguments.of(new DutyTo(null, null, START_DATE, END_DATE)),
                Arguments.of(new DutyTo(null, ID, null, END_DATE)),
                Arguments.of(new DutyTo(null, ID, START_DATE, null))
        );
    }

    public static Stream<Arguments> provideInvalidUpdateDutyToList() {
        return Stream.of(
                null,
                Arguments.of(new DutyTo(null, ID, START_DATE, END_DATE)),
                Arguments.of(new DutyTo(ID, null, START_DATE, END_DATE)),
                Arguments.of(new DutyTo(ID, ID, null, END_DATE)),
                Arguments.of(new DutyTo(ID, ID, START_DATE, null))
        );
    }
}
