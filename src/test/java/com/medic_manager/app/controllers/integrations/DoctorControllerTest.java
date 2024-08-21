package com.medic_manager.app.controllers.integrations;

import com.medic_manager.app.IntegrationTestConfig;
import com.medic_manager.app.common.ErrorResponseUtil;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.repositories.DoctorRepo;
import com.medic_manager.app.tos.DoctorTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.List;

import static com.medic_manager.app.testdata.DoctorTestdata.mockCreateDoctorTo;
import static com.medic_manager.app.testdata.DoctorTestdata.mockDoctorEntityWithEmail;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTestConfig
class DoctorControllerTest {

    private static final String CREATE_URL = "/com/medic-manager/app/createDoctor";
    private static final String EMAIL_1 = "email1@example.com";
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    DoctorRepo doctorRepo;

    @AfterEach
    void cleanup() {
        doctorRepo.deleteAll();
    }

    private HttpEntity<DoctorTo> getRequest(DoctorTo doctorTo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(doctorTo, headers);
    }

    @Nested
    class createsDoctor {
        @Test
        void createDoctor() {
            //given
            DoctorTo doctor = mockCreateDoctorTo(EMAIL_1);
            HttpEntity<DoctorTo> request = getRequest(doctor);
            //when
            ResponseEntity<DoctorTo> response = restTemplate.postForEntity(CREATE_URL, request, DoctorTo.class);
            //then
            DoctorTo createdDoctor = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(createdDoctor).isNotNull();
            assertThat(createdDoctor.id()).isNotNull();
            assertThat(createdDoctor.email()).isEqualTo(doctor.email());
            List<DoctorEntity> doctors = doctorRepo.findAll();
            assertThat(doctors).hasSize(1);
        }

        @ParameterizedTest
        @MethodSource("com.medic_manager.app.testdata.DoctorTestdata#provideInvalidCreateDoctorToList")
        void returnBadRequestWhenCreateDoctorWithIncorrectTo(DoctorTo doctorTo) {
            //given
            HttpEntity<DoctorTo> request = getRequest(doctorTo);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseBody).isNotNull();
            List<DoctorEntity> doctors = doctorRepo.findAll();
            assertThat(doctors).isEmpty();
        }

        @Test
        void returnConflictWhenCreateDoctorWithAlreadyExistingEmail() {
            //given
            DoctorEntity existingDoctor = mockDoctorEntityWithEmail(EMAIL_1);
            doctorRepo.save(existingDoctor);
            DoctorTo newDoctor = mockCreateDoctorTo(EMAIL_1);
            HttpEntity<DoctorTo> request = getRequest(newDoctor);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(responseBody).isNotNull();
            List<DoctorEntity> doctors = doctorRepo.findAll();
            assertThat(doctors).hasSize(1);
        }
    }
}
