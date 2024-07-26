package com.medic_manager.app.services;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.repositories.DoctorRepo;
import com.medic_manager.app.tos.DoctorTo;
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

import static com.medic_manager.app.testdata.DoctorTestdata.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@UnitTestConfig
class DoctorServiceTest {

    private static final Long ID = 1L;
    private static final String EMAIL_1 = "email1@example.com";
    private static final String EMAIL_2 = "email2@example.com";
    private final ArgumentCaptor<DoctorEntity> captor = ArgumentCaptor.forClass(DoctorEntity.class);
    @Mock
    private DoctorRepo doctorRepo;
    @InjectMocks
    private DoctorService doctorService;

    @Test
    void createDoctor() {
        //given
        DoctorTo doctorTo = mockCreateDoctorTo(EMAIL_1);
        //when
        when(doctorRepo.findByEmailIgnoreCase(EMAIL_1)).thenReturn(List.of());
        doctorService.createDoctor(doctorTo);
        verify(doctorRepo).save(captor.capture());
        DoctorEntity capturedEntity = captor.getValue();
        //then
        assertThat(capturedEntity.getName()).isEqualTo(doctorTo.name());
        assertThat(capturedEntity.getSurname()).isEqualTo(doctorTo.surname());
        assertThat(capturedEntity.getEmail()).isEqualTo(doctorTo.email());
        assertThat(capturedEntity.getSpecializationEnums()).hasSize(doctorTo.specializationEnums().size());
        assertThat(capturedEntity.getImageUrl()).isEqualTo(doctorTo.imageUrl());
    }

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.DoctorTestdata#provideInvalidCreateDoctorToList")
    void throwsIllegalArgumentExceptionWhenCreateDoctorWithIncorrectTo(DoctorTo doctorTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> doctorService.createDoctor(doctorTo)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsEntityExistExceptionWhenCreateDoctorWithAlreadyExistingEmail() {
        //given
        DoctorEntity doctorEntity = mockDoctorEntityWithEmail(EMAIL_1);
        DoctorTo doctorTo = mockCreateDoctorTo(EMAIL_1);
        //when
        when(doctorRepo.findByEmailIgnoreCase(EMAIL_1)).thenReturn(List.of(doctorEntity));
        //then
        assertThatThrownBy(
                () -> doctorService.createDoctor(doctorTo)
        ).isInstanceOf(EntityExistsException.class);
    }

    @Test
    void returnEmptyListWhenNoDoctorsFound() {
        //given
        //when
        when(doctorRepo.findAll()).thenReturn(List.of());
        List<DoctorEntity> doctors = doctorService.getAllDoctors();
        //then
        assertThat(doctors).isEmpty();
    }

    @Test
    void returnAllDoctors() {
        //given
        DoctorEntity doctorEntity1 = mockDoctorEntityWithEmail(EMAIL_1);
        DoctorEntity doctorEntity2 = mockDoctorEntityWithEmail(EMAIL_2);
        //when
        when(doctorRepo.findAll()).thenReturn(List.of(doctorEntity1, doctorEntity2));
        List<DoctorEntity> doctors = doctorService.getAllDoctors();
        //then
        assertThat(doctors)
                .isNotEmpty()
                .hasSize(2)
                .containsExactlyInAnyOrder(doctorEntity1, doctorEntity2);
    }

    @Test
    void returnDoctorById() {
        //given
        DoctorEntity doctorEntity = mockDoctorEntityWithIdAndEmail(ID, EMAIL_1);
        //when
        when(doctorRepo.findById(ID)).thenReturn(Optional.of(doctorEntity));
        DoctorEntity doctorById = doctorService.getDoctorById(ID);
        //then
        assertThat(doctorById.getId()).isEqualTo(ID);
    }

    @Test
    void throwsIllegalArgumentExceptionWhenGetDoctorByNullId() {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> doctorService.getDoctorById(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsEntityNotFoundExceptionWhenGetDoctorById() {
        //given
        //when
        when(doctorRepo.findById(ID)).thenReturn(Optional.empty());
        //then
        assertThatThrownBy(
                () -> doctorService.getDoctorById(ID)
        ).isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    void updateDoctor() {
        //given
        DoctorTo doctorTo = mockUpdateDoctorTo(ID, EMAIL_1);
        //when
        when(doctorRepo.findByEmailIgnoreCase(EMAIL_1)).thenReturn(List.of());
        doctorService.updateDoctor(doctorTo);
        verify(doctorRepo).save(captor.capture());
        DoctorEntity capturedEntity = captor.getValue();
        //then
        assertThat(capturedEntity.getId()).isEqualTo(doctorTo.id());
        assertThat(capturedEntity.getName()).isEqualTo(doctorTo.name());
        assertThat(capturedEntity.getSurname()).isEqualTo(doctorTo.surname());
        assertThat(capturedEntity.getEmail()).isEqualTo(doctorTo.email());
        assertThat(capturedEntity.getSpecializationEnums()).hasSize(doctorTo.specializationEnums().size());
        assertThat(capturedEntity.getImageUrl()).isEqualTo(doctorTo.imageUrl());
    }

    @ParameterizedTest
    @MethodSource("com.medic_manager.app.testdata.DoctorTestdata#provideInvalidUpdateDoctorToList")
    void throwsIllegalArgumentExceptionWhenUpdateDoctorWithIncorrectTo(DoctorTo doctorTo) {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> doctorService.updateDoctor(doctorTo)
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void throwsEntityExistExceptionWhenUpdateDoctorWithAlreadyExistingEmail() {
        //given
        DoctorEntity doctorEntity = mockDoctorEntityWithIdAndEmail(ID, EMAIL_1);
        DoctorTo doctorTo = mockUpdateDoctorTo(ID, EMAIL_1);
        //when
        when(doctorRepo.findByEmailIgnoreCase(EMAIL_1)).thenReturn(List.of(doctorEntity));
        //then
        assertThatThrownBy(
                () -> doctorService.updateDoctor(doctorTo)
        ).isInstanceOf(EntityExistsException.class);
    }

    @Test
    void deleteDoctor() {
        //given
        //when
        doctorService.deleteDoctor(ID);
        //then
        verify(doctorRepo, times(1)).deleteById(ID);
    }

    @Test
    void throwsIllegalArgumentExceptionWhenDeleteDoctorByNullId() {
        //given
        //when
        //then
        assertThatThrownBy(
                () -> doctorService.deleteDoctor(null)
        ).isInstanceOf(IllegalArgumentException.class);
    }
}
