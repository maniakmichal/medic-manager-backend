package com.medic_manager.app.controllers.integrations;

import com.medic_manager.app.IntegrationTestConfig;
import com.medic_manager.app.common.ErrorResponseUtil;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.DutyEntity;
import com.medic_manager.app.mappers.DutyMapper;
import com.medic_manager.app.repositories.DoctorRepo;
import com.medic_manager.app.repositories.DutyRepo;
import com.medic_manager.app.testdata.DoctorTestdata;
import com.medic_manager.app.testdata.DutyTestdata;
import com.medic_manager.app.tos.DutyTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTestConfig
class DutyControllerTest {

    private static final String CREATE_URL = "/com/medic-manager/app/create-duty";
    private static final String GET_ALL_URL = "/com/medic-manager/app/duties";
    private static final String GET_BY_ID_URL = "/com/medic-manager/app/duty/";
    private static final String UPDATE_URL = "/com/medic-manager/app/update-duty";
    private static final String DELETE_URL = "/com/medic-manager/app/delete-duty/";
    private static final String EMAIL = "email@example.com";
    private static final String EMAIL2 = "email2@example.com";
    private static final Long ID = 1L;
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private DutyRepo dutyRepo;
    @Autowired
    private DutyMapper dutyMapper;
    @Autowired
    private DoctorRepo doctorRepo;

    @AfterEach
    void cleanup() {
        dutyRepo.deleteAll();
        doctorRepo.deleteAll();
    }

    private HttpEntity<DutyTo> createRequestBody(DutyTo dutyTo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(dutyTo, headers);
    }

    @Nested
    class createsDuty {
        @Test
        void createDuty() {
            //given
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity();
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            DutyTo duty = DutyTestdata.mockDutyTo(null, savedDoctor.getId());
            HttpEntity<DutyTo> request = createRequestBody(duty);
            //when
            ResponseEntity<DutyTo> response = restTemplate.postForEntity(CREATE_URL, request, DutyTo.class);
            //then
            DutyTo createdDuty = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(createdDuty).isNotNull();
            assertThat(createdDuty.id()).isNotNull();
            assertThat(createdDuty.doctorId()).isEqualTo(duty.doctorId());
            assertThat(createdDuty.startDate()).isEqualTo(duty.startDate());
            assertThat(createdDuty.endDate()).isEqualTo(duty.endDate());
        }

        @ParameterizedTest
        @MethodSource("com.medic_manager.app.testdata.DutyTestdata#provideInvalidCreateDutyToList")
        void returnBadRequestWhenCreateDutyWithIncorrectTo(DutyTo dutyTo) {
            //given
            HttpEntity<DutyTo> request = createRequestBody(dutyTo);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseBody).isNotNull();
            List<DutyEntity> duties = dutyRepo.findAll();
            assertThat(duties).isEmpty();
        }

        @Test
        void returnForbiddenWhenCreateDutyWithIncorrectDates() {
            //given
            LocalDate startDate = LocalDate.of(2024, 5, 11);
            LocalDate endDate = LocalDate.of(2024, 5, 2);
            DutyTo dutyTo = DutyTestdata.mockDutyTo(null, ID, startDate, endDate);
            HttpEntity<DutyTo> request = createRequestBody(dutyTo);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseBody).isNotNull();
            List<DutyEntity> duties = dutyRepo.findAll();
            assertThat(duties).isEmpty();
        }

        @Test
        void returnNotFoundWhenCreateDutyWithDoctorNotFound() {
            //given
            DutyTo duty = DutyTestdata.mockDutyTo(null, ID);
            HttpEntity<DutyTo> request = createRequestBody(duty);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseBody).isNotNull();
            List<DutyEntity> duties = dutyRepo.findAll();
            assertThat(duties).isEmpty();
        }

        @Test
        void returnForbiddenWhenCreateDutyWithBusyDoctor() {
            //given
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity();
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            DutyEntity savedDuty = DutyTestdata.mockDutyEntity(null, savedDoctor);
            dutyRepo.save(savedDuty);
            LocalDate startDate = savedDuty.getStartDate().plusDays(5);
            LocalDate endDate = savedDuty.getEndDate().plusDays(5);
            DutyTo duty = DutyTestdata.mockDutyTo(null, savedDoctor.getId(), startDate, endDate);
            HttpEntity<DutyTo> request = createRequestBody(duty);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseBody).isNotNull();
            List<DutyEntity> duties = dutyRepo.findAll();
            assertThat(duties).hasSize(1);
        }
    }

    @Nested
    class getsAllDuties {
        @Test
        void getAllDuties() {
            //given
            DoctorEntity doctor1 = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor1 = doctorRepo.save(doctor1);
            DoctorEntity doctor2 = DoctorTestdata.mockDoctorEntity(EMAIL2);
            DoctorEntity savedDoctor2 = doctorRepo.save(doctor2);
            DutyEntity duty1 = DutyTestdata.mockDutyEntity(null, savedDoctor1);
            DutyEntity duty2 = DutyTestdata.mockDutyEntity(null, savedDoctor2);
            dutyRepo.saveAll(List.of(duty1, duty2));
            //when
            ResponseEntity<List<DutyTo>> response = restTemplate.exchange(
                    GET_ALL_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            //then
            List<DutyTo> duties = response.getBody();
            DutyTo expectedDuty1 = dutyMapper.toDutyTo(duty1);
            DutyTo expectedDuty2 = dutyMapper.toDutyTo(duty2);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(duties).hasSize(2);
            assertThat(duties)
                    .usingRecursiveFieldByFieldElementComparator()
                    .containsExactlyInAnyOrder(expectedDuty1, expectedDuty2);
        }

        @Test
        void returnEmptyListWhenNoDutiesFound() {
            //given
            //when
            ResponseEntity<List<DutyTo>> response = restTemplate.exchange(
                    GET_ALL_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            //then
            List<DutyTo> duties = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(duties).isEmpty();
        }
    }

    @Nested
    class getsDutyById {
        @Test
        void getDutyById() {
            //given
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity();
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            DutyEntity dutyEntity = DutyTestdata.mockDutyEntity(null, savedDoctor);
            DutyEntity savedDuty = dutyRepo.save(dutyEntity);
            //when
            ResponseEntity<DutyTo> response = restTemplate.getForEntity(GET_BY_ID_URL + savedDuty.getId(), DutyTo.class);
            //then
            DutyTo dutyById = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            DutyTo expectedDuty = dutyMapper.toDutyTo(savedDuty);
            assertThat(dutyById).usingRecursiveComparison().isEqualTo(expectedDuty);
        }

        @Test
        void returnBadRequestWhenGetDutyByNullId() {
            //given
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.getForEntity(GET_BY_ID_URL + null, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseBody).isNotNull();
        }

        @Test
        void returnNotFoundWhenGetDutyByNotExistingId() {
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
    class updatesDuty {
        @Test
        void updateDuty() {
            //given
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity();
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            DutyEntity dutyEntity = DutyTestdata.mockDutyEntity(null, savedDoctor);
            DutyEntity savedDuty = dutyRepo.save(dutyEntity);
            LocalDate updatedStartDate = LocalDate.of(2022, 8, 19);
            LocalDate updatedEndDate = LocalDate.of(2022, 8, 29);
            DutyTo updateDutyTo = DutyTestdata.mockDutyTo(
                    savedDuty.getId(),
                    savedDoctor.getId(),
                    updatedStartDate,
                    updatedEndDate
            );
            HttpEntity<DutyTo> request = createRequestBody(updateDutyTo);
            //when
            ResponseEntity<DutyTo> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    DutyTo.class
            );
            //then
            DutyTo updatedDuty = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(updatedDuty).isNotNull();
            assertThat(updatedDuty.id()).isEqualTo(savedDuty.getId());
            assertThat(updatedDuty.doctorId()).isEqualTo(savedDoctor.getId());
            assertThat(updatedDuty.startDate()).isNotEqualTo(savedDuty.getStartDate());
            assertThat(updatedDuty.startDate()).isEqualTo(updatedStartDate);
            assertThat(updatedDuty.endDate()).isNotEqualTo(savedDuty.getEndDate());
            assertThat(updatedDuty.endDate()).isEqualTo(updatedEndDate);
            List<DutyEntity> duties = dutyRepo.findAll();
            assertThat(duties).hasSize(1);
        }

        @Test
        void updateDutyWithNewDoctor() {
            //given
            DoctorEntity doctor1 = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor1 = doctorRepo.save(doctor1);
            DoctorEntity doctor2 = DoctorTestdata.mockDoctorEntity(EMAIL2);
            DoctorEntity savedDoctor2 = doctorRepo.save(doctor2);
            DutyEntity dutyEntity = DutyTestdata.mockDutyEntity(null, savedDoctor1);
            DutyEntity savedDuty = dutyRepo.save(dutyEntity);
            DutyTo updateDutyTo = DutyTestdata.mockDutyTo(
                    savedDuty.getId(),
                    savedDoctor2.getId()
            );
            HttpEntity<DutyTo> request = createRequestBody(updateDutyTo);
            //when
            ResponseEntity<DutyTo> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    DutyTo.class
            );
            //then
            DutyTo updatedDuty = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(updatedDuty).isNotNull();
            assertThat(updatedDuty.id()).isEqualTo(savedDuty.getId());
            assertThat(updatedDuty.doctorId()).isEqualTo(savedDoctor2.getId());
            assertThat(updatedDuty.doctorId()).isNotEqualTo(savedDoctor1.getId());
            List<DutyEntity> duties = dutyRepo.findAll();
            assertThat(duties).hasSize(1);
        }

        @ParameterizedTest
        @MethodSource("com.medic_manager.app.testdata.DutyTestdata#provideInvalidUpdateDutyToList")
        void returnBadRequestWhenUpdateDutyWithIncorrectTo(DutyTo dutyTo) {
            //given
            HttpEntity<DutyTo> request = createRequestBody(dutyTo);
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
            List<DutyEntity> duties = dutyRepo.findAll();
            assertThat(duties).isEmpty();
        }

        @Test
        void returnForbiddenWhenUpdateDutyWithIncorrectDates() {
            //given
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity();
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            DutyEntity dutyEntity = DutyTestdata.mockDutyEntity(null, savedDoctor);
            DutyEntity savedDuty = dutyRepo.save(dutyEntity);
            LocalDate updatedStartDate = LocalDate.of(2022, 8, 29);
            LocalDate updatedEndDate = LocalDate.of(2022, 8, 19);
            DutyTo updateDutyTo = DutyTestdata.mockDutyTo(
                    savedDuty.getId(),
                    savedDoctor.getId(),
                    updatedStartDate,
                    updatedEndDate
            );
            HttpEntity<DutyTo> request = createRequestBody(updateDutyTo);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    ErrorResponseUtil.class
            );
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseBody).isNotNull();
        }

        @Test
        void returnNotFoundWhenUpdateDutyWithDoctorNotFound() {
            //given
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity();
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            DutyEntity dutyEntity = DutyTestdata.mockDutyEntity(null, savedDoctor);
            DutyEntity savedDuty = dutyRepo.save(dutyEntity);
            DutyTo dutyTo = DutyTestdata.mockDutyTo(savedDuty.getId(), Long.MAX_VALUE);
            HttpEntity<DutyTo> request = createRequestBody(dutyTo);
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
        }

        @Test
        void returnForbiddenWhenUpdateDutyWithBusyDoctor() {
            //given
            DoctorEntity doctor1 = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor1 = doctorRepo.save(doctor1);
            DoctorEntity doctor2 = DoctorTestdata.mockDoctorEntity(EMAIL2);
            DoctorEntity savedDoctor2 = doctorRepo.save(doctor2);
            DutyEntity dutyEntity1 = DutyTestdata.mockDutyEntity(null, savedDoctor1);
            DutyEntity savedDuty1 = dutyRepo.save(dutyEntity1);
            DutyEntity dutyEntity2 = DutyTestdata.mockDutyEntity(null, savedDoctor2);
            dutyRepo.save(dutyEntity2);
            DutyTo updateDutyTo = DutyTestdata.mockDutyTo(
                    savedDuty1.getId(),
                    savedDoctor2.getId()
            );
            HttpEntity<DutyTo> request = createRequestBody(updateDutyTo);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    ErrorResponseUtil.class
            );
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseBody).isNotNull();
        }
    }

    @Nested
    class deletesDuty {
        @Test
        void deleteDuty() {
            //given
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity();
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            DutyEntity dutyEntity = DutyTestdata.mockDutyEntity(null, savedDoctor);
            DutyEntity savedDuty = dutyRepo.save(dutyEntity);
            //when
            ResponseEntity<Void> response = restTemplate.exchange(
                    DELETE_URL + savedDuty.getId(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Optional<DutyEntity> deletedDuty = dutyRepo.findById(savedDuty.getId());
            assertThat(deletedDuty).isNotPresent();
            List<DutyEntity> allDuties = dutyRepo.findAll();
            assertThat(allDuties).isEmpty();
        }

        @Test
        void returnBadRequestWhenDeleteDutyByNullId() {
            //given
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.exchange(
                    DELETE_URL + null,
                    HttpMethod.DELETE,
                    null,
                    ErrorResponseUtil.class
            );
            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        }

        @Test
        void returnNoContentWhenDeleteDutyByNotExistingId() {
            //given
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            DutyEntity dutyEntity = DutyTestdata.mockDutyEntity(null, savedDoctor);
            DutyEntity savedDuty = dutyRepo.save(dutyEntity);
            //when
            ResponseEntity<Void> response = restTemplate.exchange(
                    DELETE_URL + Long.MAX_VALUE,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Optional<DutyEntity> notDeletedDuty = dutyRepo.findById(savedDuty.getId());
            assertThat(notDeletedDuty).isPresent();
            List<DutyEntity> allDuties = dutyRepo.findAll();
            assertThat(allDuties).hasSize(1);
        }
    }
}
