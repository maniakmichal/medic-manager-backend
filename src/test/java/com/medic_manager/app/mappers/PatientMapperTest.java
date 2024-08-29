package com.medic_manager.app.mappers;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.tos.PatientTo;
import org.junit.jupiter.api.Test;

import static com.medic_manager.app.testdata.PatientTestdata.mockPatientEntity;
import static org.assertj.core.api.Assertions.assertThat;

@UnitTestConfig
class PatientMapperTest {

    private final PatientMapper patientMapper = new PatientMapper();

    @Test
    void returnMappedPatientEntity() {
        //given
        Long id = 1L;
        String email = "email@example.com";
        PatientEntity patientEntity = mockPatientEntity(id, email);
        //when
        PatientTo patientTo = patientMapper.toPatientTo(patientEntity);
        //then
        assertThat(patientTo)
                .usingRecursiveComparison()
                .isEqualTo(patientEntity);
    }

}
