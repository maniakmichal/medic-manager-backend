package com.medic_manager.app.mappers;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.testdata.DoctorTestdata;
import com.medic_manager.app.tos.DoctorTo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DoctorMapper.class)
class DoctorMapperTest {

    @Autowired
    private DoctorMapper doctorMapper;

    @Test
    void returnMappedDoctorEntity() {
        //given
        Long id = 1L;
        String email = "email@email.com";
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntityWithIdAndEmail(id, email);
        //when
        DoctorTo doctorTo = doctorMapper.toDoctorTo(doctorEntity);
        //then
        assertThat(doctorTo)
                .usingRecursiveComparison()
                .isEqualTo(doctorEntity);
    }

}
