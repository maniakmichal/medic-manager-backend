package com.medic_manager.app.services;

import com.medic_manager.app.UnitTestConfig;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.repositories.DoctorRepo;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@UnitTestConfig
class DoctorServiceTest {

    @Mock
    private DoctorRepo doctorRepo;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void returnEmptyListWhenNoDoctorsFound() {
        //given
        //when
        when(doctorRepo.findAll()).thenReturn(List.of());
        List<DoctorEntity> doctors = doctorService.getAllDoctors();
        //then
        assertThat(doctors).isEmpty();
    }

}
