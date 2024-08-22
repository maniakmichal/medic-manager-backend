package com.medic_manager.app.controllers.integrations;

import com.medic_manager.app.IntegrationTestConfig;
import com.medic_manager.app.common.ErrorResponseUtil;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.mappers.DoctorMapper;
import com.medic_manager.app.repositories.DoctorRepo;
import com.medic_manager.app.tos.DoctorTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.List;

import static com.medic_manager.app.testdata.DoctorTestdata.*;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTestConfig
class DoctorControllerTest {

    private static final String CREATE_URL = "/com/medic-manager/app/createDoctor";
    private static final String GET_ALL_URL = "/com/medic-manager/app/doctors";
    private static final String GET_BY_ID_URL = "/com/medic-manager/app/doctor/";
    private static final String UPDATE_URL = "/com/medic-manager/app/updateDoctor";
    private static final String EMAIL_1 = "email1@example.com";
    private static final String EMAIL_2 = "email2@example.com";
    @Autowired
    TestRestTemplate restTemplate;
    @Autowired
    DoctorRepo doctorRepo;
    @Autowired
    DoctorMapper doctorMapper;

    @AfterEach
    void cleanup() {
        doctorRepo.deleteAll();
    }

    private HttpEntity<DoctorTo> createRequestBody(DoctorTo doctorTo) {
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
            HttpEntity<DoctorTo> request = createRequestBody(doctor);
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
            HttpEntity<DoctorTo> request = createRequestBody(doctorTo);
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
            HttpEntity<DoctorTo> request = createRequestBody(newDoctor);
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

    @Nested
    class getsAllDoctors {
        @Test
        void getAllDoctors() {
            //given
            DoctorEntity doctor1 = mockDoctorEntityWithEmail(EMAIL_1);
            DoctorEntity doctor2 = mockDoctorEntityWithEmail(EMAIL_2);
            doctorRepo.saveAll(List.of(doctor1, doctor2));
            //when
            ResponseEntity<List<DoctorTo>> response = restTemplate.exchange(
                    GET_ALL_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DoctorTo>>() {
                    }
            );
            //then
            List<DoctorTo> doctors = response.getBody();
            DoctorTo expectedDoctor1 = doctorMapper.toDoctorTo(doctor1);
            DoctorTo expectedDoctor2 = doctorMapper.toDoctorTo(doctor2);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(doctors).hasSize(2);
            assertThat(doctors).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(expectedDoctor1, expectedDoctor2);
        }

        @Test
        void returnEmptyListWhenNoDoctorsFound() {
            //given
            //when
            ResponseEntity<List<DoctorTo>> response = restTemplate.exchange(
                    GET_ALL_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<DoctorTo>>() {
                    }
            );
            //then
            List<DoctorTo> doctors = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(doctors).isEmpty();
        }
    }

    @Nested
    class getsDoctorById {
        @Test
        void getDoctorById() {
            //given
            DoctorEntity doctor = mockDoctorEntityWithEmail(EMAIL_1);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            //when
            ResponseEntity<DoctorTo> response = restTemplate.getForEntity(GET_BY_ID_URL + savedDoctor.getId(), DoctorTo.class);
            //then
            DoctorTo doctorById = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            DoctorTo expectedDoctor = doctorMapper.toDoctorTo(savedDoctor);
            assertThat(doctorById).usingRecursiveComparison().isEqualTo(expectedDoctor);
        }

        @Test
        void returnBadRequestWhenGetDoctorByNullId() {
            //given
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.getForEntity(GET_BY_ID_URL + null, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseBody).isNotNull();
        }

        @Test
        void returnNotFoundWhenGetDoctorByNotExistingId() {
            //given
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.getForEntity(GET_BY_ID_URL + Long.MAX_VALUE, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseBody).isNotNull();
        }
    }

    @Nested
    class updatesDoctor {
        @Test
        void updateDoctor() {
            //given
            DoctorEntity doctorEntity = mockDoctorEntityWithEmail(EMAIL_1);
            DoctorEntity savedDoctor = doctorRepo.save(doctorEntity);
            DoctorTo updateDoctorTo = mockUpdateDoctorTo(savedDoctor.getId(), EMAIL_2);
            HttpEntity<DoctorTo> request = createRequestBody(updateDoctorTo);
            //when
            ResponseEntity<DoctorTo> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    DoctorTo.class
            );
            //then
            DoctorTo updatedDoctor = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(updatedDoctor).isNotNull();
            assertThat(updatedDoctor.id()).isEqualTo(savedDoctor.getId());
            assertThat(updatedDoctor.email()).isNotEqualTo(savedDoctor.getEmail());
            List<DoctorEntity> doctors = doctorRepo.findAll();
            assertThat(doctors).hasSize(1);
            assertThat(doctors.get(0).getEmail()).isEqualTo(updatedDoctor.email());
        }

        @ParameterizedTest
        @MethodSource("com.medic_manager.app.testdata.DoctorTestdata#provideInvalidUpdateDoctorToList")
        void returnBadRequestWhenUpdateDoctorWithIncorrectTo(DoctorTo doctorTo) {
            //given
            HttpEntity<DoctorTo> request = createRequestBody(doctorTo);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    ErrorResponseUtil.class
            );
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseBody).isNotNull();
            List<DoctorEntity> doctors = doctorRepo.findAll();
            assertThat(doctors).isEmpty();
        }

        @Test
        void returnConflictWhenUpdateDoctorWithAlreadyExistingEmail() {
            //given
            DoctorEntity doctor = mockDoctorEntityWithEmail(EMAIL_1);
            DoctorEntity existingDoctor = doctorRepo.save(doctor);
            DoctorTo updateDoctor = mockUpdateDoctorTo(existingDoctor.getId(), EMAIL_1);
            HttpEntity<DoctorTo> request = createRequestBody(updateDoctor);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    ErrorResponseUtil.class
            );
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(responseBody).isNotNull();
            List<DoctorEntity> doctors = doctorRepo.findAll();
            assertThat(doctors).hasSize(1);
        }

        @Test
        void returnBadRequestWhenUpdateDoctorWithNotExistingId() {
            //given
            DoctorEntity doctor = mockDoctorEntityWithEmail(EMAIL_1);
            doctorRepo.save(doctor);
            DoctorTo updateDoctor = mockUpdateDoctorTo(Long.MAX_VALUE, EMAIL_2);
            HttpEntity<DoctorTo> request = createRequestBody(updateDoctor);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    ErrorResponseUtil.class
            );
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseBody).isNotNull();
            List<DoctorEntity> doctors = doctorRepo.findAll();
            assertThat(doctors).hasSize(1);
            assertThat(doctors.get(0).getEmail()).isEqualTo(EMAIL_1);
        }
    }
}
