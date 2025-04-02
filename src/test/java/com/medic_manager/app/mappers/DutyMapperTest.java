package com.medic_manager.app.mappers;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.DutyEntity;
import com.medic_manager.app.tos.DutyTo;
import org.junit.jupiter.api.Test;

import static com.medic_manager.app.testdata.DutyTestdata.mockDutyEntity;
import static org.assertj.core.api.Assertions.assertThat;

@UnitTestConfig
class DutyMapperTest {

    private final DutyMapper dutyMapper = new DutyMapper();

    @Test
    void returnedMappedDutyEntity() {
        //given
        DutyEntity dutyEntity = mockDutyEntity();
        //when
        DutyTo dutyTo = dutyMapper.toDutyTo(dutyEntity);
        //then
        assertThat(dutyTo)
                .usingRecursiveComparison()
                .ignoringFields("doctorId")
                .isEqualTo(dutyEntity);
    }
}
