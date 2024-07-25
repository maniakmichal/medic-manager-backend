package com.medic_manager.app.mappers;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.tos.DoctorTo;
import org.junit.jupiter.api.Test;

import static com.medic_manager.app.testdata.DoctorTestdata.mockDoctorEntityWithIdAndEmail;
import static org.assertj.core.api.Assertions.assertThat;

@UnitTestConfig
class DoctorMapperTest {

    private final DoctorMapper doctorMapper = new DoctorMapper();

    @Test
    void returnMappedDoctorEntity() {
        //given
        Long id = 1L;
        String email = "email@email.com";
        DoctorEntity doctorEntity = mockDoctorEntityWithIdAndEmail(id, email);
        //when
        DoctorTo doctorTo = doctorMapper.toDoctorTo(doctorEntity);
        //then
        assertThat(doctorTo)
                .usingRecursiveComparison()
                .isEqualTo(doctorEntity);
    }

}
