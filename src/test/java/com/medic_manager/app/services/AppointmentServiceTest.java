package com.medic_manager.app.services;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.enums.AppointmentStatusEnum;
import com.medic_manager.app.exceptions.AppointmentCreationFailedBusinessException;
import com.medic_manager.app.exceptions.IncorrectDayOfWeekBusinessException;
import com.medic_manager.app.exceptions.IncorrectHourOrMinutesBusinessException;
import com.medic_manager.app.repositories.AppointmentRepo;
import com.medic_manager.app.testdata.AppointmentTestdata;
import com.medic_manager.app.testdata.DoctorTestdata;
import com.medic_manager.app.testdata.PatientTestdata;
import com.medic_manager.app.tos.AppointmentTo;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@UnitTestConfig
class AppointmentServiceTest {

    private static final Long ID = 1L;
    private static final Long ID_2 = 2L;
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

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidHourList")
    void throwsIncorrectHourOrMinutesBusinessExceptionWhenCreateAppointmentWithIncorrectHour(AppointmentTo appointmentTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> appointmentService.createAppointment(appointmentTo)
        ).isInstanceOf(IncorrectHourOrMinutesBusinessException.class);
    }

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidMinuteList")
    void throwsIncorrectHourOrMinutesBusinessExceptionWhenCreateAppointmentWithIncorrectMinute(AppointmentTo appointmentTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> appointmentService.createAppointment(appointmentTo)
        ).isInstanceOf(IncorrectHourOrMinutesBusinessException.class);
    }

    @Test
    void throwsEntityNotFoundExceptionWhenCreateAppointmentWithoutPatientFound() {
        //given
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo();
        //when
        when(patientService.getPatientById(ID)).thenThrow(new EntityNotFoundException());
        //then
        assertThatThrownBy(
                () -> appointmentService.createAppointment(appointmentTo)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void throwsEntityNotFoundExceptionWhenCreateAppointmentWithoutDoctorFound() {
        //given
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo();
        PatientEntity patientEntity = PatientTestdata.mockPatientEntity(ID, EMAIL);
        //when
        when(patientService.getPatientById(ID)).thenReturn(patientEntity);
        when(doctorService.getDoctorById(ID)).thenThrow(new EntityNotFoundException());
        //then
        assertThatThrownBy(
                () -> appointmentService.createAppointment(appointmentTo)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void throwsAppointmentCreationFailedBusinessExceptionWhenCreateAppointmentWithBusyPatient() {
        //given
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo();
        PatientEntity patientEntity = PatientTestdata.mockPatientEntity(ID, EMAIL);
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(ID, EMAIL);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity();
        //when
        when(patientService.getPatientById(ID)).thenReturn(patientEntity);
        when(doctorService.getDoctorById(ID)).thenReturn(doctorEntity);
        when(appointmentRepo.findAllByPatientEntityAndAppointmentDate(patientEntity, appointmentTo.appointmentDate()))
                .thenReturn(List.of(appointmentEntity));
        //then
        assertThatThrownBy(
                () -> appointmentService.createAppointment(appointmentTo)
        ).isInstanceOf(AppointmentCreationFailedBusinessException.class);
    }

    @Test
    void throwsAppointmentCreationFailedBusinessExceptionWhenCreateAppointmentWithBusyDoctor() {
        //given
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo();
        PatientEntity patientEntity = PatientTestdata.mockPatientEntity(ID, EMAIL);
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(ID, EMAIL);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity();
        //when
        when(patientService.getPatientById(ID)).thenReturn(patientEntity);
        when(doctorService.getDoctorById(ID)).thenReturn(doctorEntity);
        when(appointmentRepo.findAllByPatientEntityAndAppointmentDate(patientEntity, appointmentTo.appointmentDate()))
                .thenReturn(List.of());
        when(appointmentRepo.findAllByDoctorEntityAndAppointmentDate(doctorEntity, appointmentTo.appointmentDate()))
                .thenReturn(List.of(appointmentEntity));
        //then
        assertThatThrownBy(
                () -> appointmentService.createAppointment(appointmentTo)
        ).isInstanceOf(AppointmentCreationFailedBusinessException.class);
    }

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

    @Test
    void returnAppointmentById() {
        //given
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(ID);
        //when
        when(appointmentRepo.findById(appointmentEntity.getId())).thenReturn(Optional.of(appointmentEntity));
        AppointmentEntity appointmentById = appointmentService.getAppointmentById(appointmentEntity.getId());
        //then
        assertThat(appointmentById.getId()).isEqualTo(appointmentEntity.getId());
    }

    @Test
    void throwsIllegalArgumentExceptionWhenGetAppointmentByNullId() {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> appointmentService.getAppointmentById(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsEntityNotFoundExceptionWhenGetAppointmentById() {
        //given
        //when
        when(appointmentRepo.findById(ID)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(
                () -> appointmentService.getAppointmentById(ID)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateAppointment() {
        //given
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo(
                ID,
                LocalDate.of(2024, 12, 18),
                DayOfWeek.FRIDAY,
                (byte) 10,
                (byte) 45,
                AppointmentStatusEnum.IN_PROGRESS,
                ID,
                ID
        );
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(ID);
        //when
        when(appointmentRepo.findById(ID)).thenReturn(Optional.of(appointmentEntity));
        appointmentService.updateAppointment(appointmentTo);
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
    @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidUpdateAppointmentToList")
    void throwsIllegalArgumentExceptionWhenUpdateAppointmentWithIncorrectTo(AppointmentTo appointmentTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> appointmentService.updateAppointment(appointmentTo)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidDayOfWeekListForUpdate")
    void throwsIncorrectDayOfWeekBusinessExceptionWhenUpdateAppointmentWithIncorrectDay(AppointmentTo appointmentTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> appointmentService.updateAppointment(appointmentTo)
        ).isInstanceOf(IncorrectDayOfWeekBusinessException.class);
    }

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidHourListForUpdate")
    void throwsIncorrectHourOrMinutesBusinessExceptionWhenUpdateAppointmentWithIncorrectHour(AppointmentTo appointmentTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> appointmentService.updateAppointment(appointmentTo)
        ).isInstanceOf(IncorrectHourOrMinutesBusinessException.class);
    }

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidMinuteListForUpdate")
    void throwsIncorrectHourOrMinutesBusinessExceptionWhenUpdateAppointmentWithIncorrectMinute(AppointmentTo appointmentTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> appointmentService.updateAppointment(appointmentTo)
        ).isInstanceOf(IncorrectHourOrMinutesBusinessException.class);
    }

    @Test
    void throwsEntityNotFoundExceptionWhenUpdateAppointmentWithoutPatientFound() {
        //given
        PatientEntity oldPatient = PatientTestdata.mockPatientEntity(ID, EMAIL);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(ID);
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo(ID, ID, ID_2);
        List<AppointmentEntity> appointmentEntityList = new ArrayList<>();
        appointmentEntityList.add(appointmentEntity);
        oldPatient.setAppointmentEntityList(appointmentEntityList);
        //when
        when(appointmentRepo.findById(ID)).thenReturn(Optional.of(appointmentEntity));
        when(patientService.getPatientByIdWithAppointments(ID)).thenReturn(oldPatient);
        when(patientService.getPatientByIdWithAppointments(ID_2)).thenThrow(new EntityNotFoundException());
        //then
        assertThatThrownBy(
                () -> appointmentService.updateAppointment(appointmentTo)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void throwsEntityNotFoundExceptionWhenUpdateAppointmentWithoutDoctorFound() {
        //given
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(ID);
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo(ID, ID_2, ID);
        //when
        when(appointmentRepo.findById(ID)).thenReturn(Optional.of(appointmentEntity));
        when(doctorService.getDoctorById(ID_2)).thenThrow(new EntityNotFoundException());
        //then
        assertThatThrownBy(
                () -> appointmentService.updateAppointment(appointmentTo)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void throwsAppointmentCreationFailedBusinessExceptionWhenUpdateAppointmentWithBusyPatient() {
        //given
        PatientEntity oldPatient = PatientTestdata.mockPatientEntity(ID, EMAIL);
        PatientEntity newPatient = PatientTestdata.mockPatientEntity(ID_2, EMAIL);
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(ID, EMAIL);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(ID, doctorEntity, oldPatient);
        List<AppointmentEntity> appointmentEntityList = new ArrayList<>();
        appointmentEntityList.add(appointmentEntity);
        oldPatient.setAppointmentEntityList(appointmentEntityList);
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo(ID, ID, ID_2);
        //when
        when(appointmentRepo.findById(ID)).thenReturn(Optional.of(appointmentEntity));
        when(patientService.getPatientByIdWithAppointments(ID)).thenReturn(oldPatient);
        when(patientService.getPatientByIdWithAppointments(ID_2)).thenReturn(newPatient);
        when(appointmentRepo.findAllByPatientEntityAndAppointmentDate(newPatient, appointmentTo.appointmentDate()))
                .thenReturn(List.of(appointmentEntity));
        //then
        assertThatThrownBy(
                () -> appointmentService.updateAppointment(appointmentTo)
        ).isInstanceOf(AppointmentCreationFailedBusinessException.class);
    }

    @Test
    void throwsAppointmentCreationFailedBusinessExceptionWhenUpdateAppointmentWithBusyDoctor() {
        //given
        DoctorEntity doctorEntity = DoctorTestdata.mockDoctorEntity(ID_2, EMAIL);
        AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(ID);
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo(ID, ID_2, ID);
        //when
        when(appointmentRepo.findById(ID)).thenReturn(Optional.of(appointmentEntity));
        when(doctorService.getDoctorById(ID_2)).thenReturn(doctorEntity);
        when(appointmentRepo.findAllByDoctorEntityAndAppointmentDate(doctorEntity, appointmentTo.appointmentDate()))
                .thenReturn(List.of(appointmentEntity));
        //then
        assertThatThrownBy(
                () -> appointmentService.updateAppointment(appointmentTo)
        ).isInstanceOf(AppointmentCreationFailedBusinessException.class);
    }

    @Test
    void throwsEntityNotFoundExceptionWhenUpdateAppointmentWithoutAppointmentFound() {
        //given
        AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo(ID);
        //when
        when(appointmentRepo.findById(ID)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(
                () -> appointmentService.updateAppointment(appointmentTo)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void deleteAppointment() {
        //given
        //when
        appointmentService.deleteAppointment(ID);
        //then
        verify(appointmentRepo, times(1)).deleteById(ID);
    }

    @Test
    void throwsIllegalArgumentExceptionWhenDeleteAppointmentByNullId() {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> appointmentService.deleteAppointment(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
