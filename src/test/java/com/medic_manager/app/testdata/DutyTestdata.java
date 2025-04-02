package com.medic_manager.app.testdata;

import com.medic_manager.app.entities.DutyEntity;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;

@TestComponent
public class DutyTestdata {

    private static final LocalDate startDate = LocalDate.of(2023, 9, 27);
    private static final LocalDate endDate = LocalDate.of(2023, 10, 10);

    public static DutyEntity mockDutyEntity() {
        DutyEntity dutyEntity = new DutyEntity();
        dutyEntity.setId(null);
        dutyEntity.setStartDate(startDate);
        dutyEntity.setEndDate(endDate);
        dutyEntity.setDoctorEntity(DoctorTestdata.mockDoctorEntity());
        return dutyEntity;
    }
}
