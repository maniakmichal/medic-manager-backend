package com.medic_manager.app.mappers;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.tos.AppointmentTo;
import org.junit.jupiter.api.Test;

import static com.medic_manager.app.testdata.AppointmentTestdata.mockAppointmentEntity;
import static org.assertj.core.api.Assertions.assertThat;

@UnitTestConfig
class AppointmentMapperTest {

    private final AppointmentMapper appointmentMapper = new AppointmentMapper();

    @Test
    void returnedMappedAppointmentEntity() {
        //given
        AppointmentEntity appointmentEntity = mockAppointmentEntity();
        //when
        AppointmentTo appointmentTo = appointmentMapper.toAppointmentTo(appointmentEntity);
        //then
        assertThat(appointmentTo)
                .usingRecursiveComparison()
                .ignoringFields("patientId", "doctorId")
                .isEqualTo(appointmentEntity);
    }
}
