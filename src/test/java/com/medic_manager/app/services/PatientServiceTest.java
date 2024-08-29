package com.medic_manager.app.services;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.repositories.PatientRepo;
import com.medic_manager.app.tos.PatientTo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;
import java.util.Optional;

import static com.medic_manager.app.testdata.PatientTestdata.mockPatientEntity;
import static com.medic_manager.app.testdata.PatientTestdata.mockPatientTo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@UnitTestConfig
class PatientServiceTest {

    private static final Long ID = 1L;
    private static final String EMAIL_1 = "email1@example.com";
    private static final String EMAIL_2 = "email2@example.com";
    private final ArgumentCaptor<PatientEntity> captor = ArgumentCaptor.forClass(PatientEntity.class);
    @Mock
    private PatientRepo patientRepo;
    @InjectMocks
    private PatientService patientService;

    @Test
    void createPatient() {
        //given
        PatientTo patientTo = mockPatientTo(EMAIL_1);
        //when
        when(patientRepo.findByEmailIgnoreCase(EMAIL_1)).thenReturn(Optional.empty());
        patientService.createPatient(patientTo);
        verify(patientRepo).save(captor.capture());
        PatientEntity capturedEntity = captor.getValue();
        //then
        assertThat(capturedEntity.getName()).isEqualTo(patientTo.name());
        assertThat(capturedEntity.getSurname()).isEqualTo(patientTo.surname());
        assertThat(capturedEntity.getEmail()).isEqualTo(patientTo.email());
        assertThat(capturedEntity.getBirthdate()).isEqualTo(patientTo.birthdate());
        assertThat(capturedEntity.getGenderEnum()).isEqualTo(patientTo.genderEnum());
    }

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.PatientTestdata#provideInvalidCreatePatientToList")
    void throwsIllegalArgumentExceptionWhenCreatePatientWithIncorrectTo(PatientTo patientTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> patientService.createPatient(patientTo)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsEntityExistExceptionWhenCreatePatientWithAlreadyExistingEmail() {
        //given
        PatientEntity patientEntity = mockPatientEntity(EMAIL_1);
        PatientTo patientTo = mockPatientTo(EMAIL_1);
        //when
        when(patientRepo.findByEmailIgnoreCase(EMAIL_1)).thenReturn(Optional.of(patientEntity));
        //then
        assertThatThrownBy(
                () -> patientService.createPatient(patientTo)
        ).isInstanceOf(EntityExistsException.class);
    }

    @Test
    void returnEmptyListWhenNoPatientsFound() {
        //given
        //when
        when(patientRepo.findAll()).thenReturn(List.of());
        List<PatientEntity> patients = patientService.getAllPatients();
        //then
        assertThat(patients).isEmpty();
    }

    @Test
    void returnAllPatients() {
        //given
        PatientEntity patientEntity1 = mockPatientEntity(EMAIL_1);
        PatientEntity patientEntity2 = mockPatientEntity(EMAIL_2);
        //when
        when(patientRepo.findAll()).thenReturn(List.of(patientEntity1, patientEntity2));
        List<PatientEntity> patients = patientService.getAllPatients();
        //then
        assertThat(patients)
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(patientEntity1, patientEntity2);
    }

    @Test
    void returnPatientById() {
        //given
        PatientEntity patientEntity = mockPatientEntity(ID, EMAIL_1);
        //when
        when(patientRepo.findById(ID)).thenReturn(Optional.of(patientEntity));
        PatientEntity patientById = patientService.getPatientById(ID);
        //then
        assertThat(patientById.getId()).isEqualTo(ID);
    }

    @Test
    void throwsIllegalArgumentExceptionWhenGetPatientByNullId() {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> patientService.getPatientById(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsEntityNotFoundExceptionWhenGetPatientById() {
        //given
        //when
        when(patientRepo.findById(ID)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(
                () -> patientService.getPatientById(ID)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updatePatient() {
        //given
        PatientTo patientTo = mockPatientTo(ID, EMAIL_1);
        PatientEntity patientEntity = mockPatientEntity(ID, EMAIL_2);
        //when
        when(patientRepo.findByEmailIgnoreCase(EMAIL_1)).thenReturn(Optional.empty());
        when(patientRepo.findById(ID)).thenReturn(Optional.of(patientEntity));
        patientService.updatePatient(patientTo);
        verify(patientRepo).save(captor.capture());
        PatientEntity capturedEntity = captor.getValue();
        //then
        assertThat(capturedEntity.getId()).isEqualTo(patientTo.id());
        assertThat(capturedEntity.getName()).isEqualTo(patientTo.name());
        assertThat(capturedEntity.getSurname()).isEqualTo(patientTo.surname());
        assertThat(capturedEntity.getEmail()).isEqualTo(patientTo.email());
        assertThat(capturedEntity.getBirthdate()).isEqualTo(patientTo.birthdate());
        assertThat(capturedEntity.getGenderEnum()).isEqualTo(patientTo.genderEnum());
    }

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.PatientTestdata#provideInvalidUpdatePatientToList")
    void throwsIllegalArgumentExceptionWhenUpdatePatientWithIncorrectTo(PatientTo patientTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> patientService.updatePatient(patientTo)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsEntityExistExceptionWhenUpdatePatientWithAlreadyExistingEmail() {
        //given
        PatientEntity patientEntity = mockPatientEntity(ID, EMAIL_1);
        PatientTo patientTo = mockPatientTo(ID, EMAIL_1);
        //when
        when(patientRepo.findByEmailIgnoreCase(EMAIL_1)).thenReturn(Optional.of(patientEntity));
        //then
        assertThatThrownBy(
                () -> patientService.updatePatient(patientTo)
        ).isInstanceOf(EntityExistsException.class);
    }

    @Test
    void deletePatient() {
        //given
        //when
        patientService.deletePatient(ID);
        //then
        verify(patientRepo, times(1)).deleteById(ID);
    }

    @Test
    void throwsIllegalArgumentExceptionWhenDeleteDoctorByNullId() {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> patientService.deletePatient(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
