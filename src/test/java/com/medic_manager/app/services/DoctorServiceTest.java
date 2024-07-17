package com.medic_manager.app.services;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.repositories.DoctorRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class DoctorServiceTest {

    @MockBean
    private DoctorRepo doctorRepo;

    @Autowired
    private DoctorService doctorService;

    @BeforeEach
    void setup() {
        doctorService = new DoctorService(doctorRepo);
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

}
