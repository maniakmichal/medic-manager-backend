package com.medic_manager.app.services;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.exceptions.IncorrectDayOfWeekBusinessException;
import com.medic_manager.app.repositories.AppointmentRepo;
import com.medic_manager.app.testdata.AppointmentTestdata;
import com.medic_manager.app.testdata.DoctorTestdata;
import com.medic_manager.app.testdata.PatientTestdata;
import com.medic_manager.app.tos.AppointmentTo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@UnitTestConfig
class AppointmentServiceTest {

    private static final Long ID = 1L;
    private static final String EMAIL = "email@example.com";
    private final ArgumentCaptor<AppointmentEntity> captor = ArgumentCaptor.forClass(AppointmentEntity.class);
    @Mock
    private AppointmentRepo appointmentRepo;
    @Mock
    private DoctorService doctorService;
    @Mock
    private PatientService patientService;
    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void createAppointment() {
        //given
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo();
        PatientEntity patientEntity = PatientTestdata.mockPatientEntity(ID, EMAIL);
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(ID, EMAIL);
        //when
        when(patientService.getPatientById(ID)).thenReturn(patientEntity);
        when(doctorService.getDoctorById(ID)).thenReturn(doctorEntity);
        when(appointmentRepo.findAllByPatientEntityAndAppointmentDate(patientEntity, appointmentTo.appointmentDate()))
                .thenReturn(List.of());
        when(appointmentRepo.findAllByDoctorEntityAndAppointmentDate(doctorEntity, appointmentTo.appointmentDate()))
                .thenReturn(List.of());
        appointmentService.createAppointment(appointmentTo);
        verify(appointmentRepo).save(captor.capture());
        AppointmentEntity capturedAppointment = captor.getValue();
        //then
        assertThat(capturedAppointment.getAppointmentDate()).isEqualTo(appointmentTo.appointmentDate());
        assertThat(capturedAppointment.getAppointmentDayOfWeek()).isEqualTo(appointmentTo.appointmentDayOfWeek());
        assertThat(capturedAppointment.getAppointmentHour()).isEqualTo(appointmentTo.appointmentHour());
        assertThat(capturedAppointment.getAppointmentMinute()).isEqualTo(appointmentTo.appointmentMinute());
        assertThat(capturedAppointment.getAppointmentStatusEnum()).isEqualTo(appointmentTo.appointmentStatusEnum());
        assertThat(capturedAppointment.getPatientEntity().getId()).isEqualTo(appointmentTo.patientId());
        assertThat(capturedAppointment.getDoctorEntity().getId()).isEqualTo(appointmentTo.doctorId());
    }

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidCreateAppointmentToList")
    void throwsIllegalArgumentExceptionWhenCreateAppointmentWithIncorrectTo(AppointmentTo appointmentTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> appointmentService.createAppointment(appointmentTo)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidDayOfWeekList")
    void throwsIncorrectDayOfWeekBusinessExceptionWhenCreateAppointmentWithIncorrectDay(AppointmentTo appointmentTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> appointmentService.createAppointment(appointmentTo)
        ).isInstanceOf(IncorrectDayOfWeekBusinessException.class);
    }

    //TODO - add parametrized tests for minutes and hours exception

    @Test
    void returnEmptyListWhenNoAppointmentsFound() {
        //given
        //when
        when(appointmentRepo.findAll()).thenReturn(List.of());
        List<AppointmentEntity> appointments = appointmentService.getAllAppointments();
        //then
        assertThat(appointments).isEmpty();
    }

    @Test
    void returnAllAppointments() {
        //given
        AppointmentEntity appointmentEntity1 = AppointmentTestdata.mockAppointmentEntity();
        AppointmentEntity appointmentEntity2 = AppointmentTestdata.mockAppointmentEntity();
        //when
        when(appointmentRepo.findAll()).thenReturn(List.of(appointmentEntity1, appointmentEntity2));
        List<AppointmentEntity> appointments = appointmentService.getAllAppointments();
        //then
        assertThat(appointments)
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(appointmentEntity1, appointmentEntity2);
    }
}
