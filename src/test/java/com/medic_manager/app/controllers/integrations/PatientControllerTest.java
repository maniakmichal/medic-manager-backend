package com.medic_manager.app.controllers.integrations;

import com.medic_manager.app.IntegrationTestConfig;
import com.medic_manager.app.common.ErrorResponseUtil;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.mappers.PatientMapper;
import com.medic_manager.app.repositories.PatientRepo;
import com.medic_manager.app.tos.PatientTo;
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
import java.util.Optional;

import static com.medic_manager.app.testdata.PatientTestdata.mockPatientEntity;
import static com.medic_manager.app.testdata.PatientTestdata.mockPatientTo;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTestConfig
class PatientControllerTest {

    private static final String CREATE_URL = "/com/medic-manager/app/create-patient";
    private static final String GET_ALL_URL = "/com/medic-manager/app/patients";
    private static final String GET_BY_ID_URL = "/com/medic-manager/app/patient/";
    private static final String UPDATE_URL = "/com/medic-manager/app/update-patient";
    private static final String DELETE_URL = "/com/medic-manager/app/delete-patient/";
    private static final String EMAIL_1 = "email1@example.com";
    private static final String EMAIL_2 = "email2@example.com";
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private PatientMapper patientMapper;

    @AfterEach
    void cleanup() {
        patientRepo.deleteAll();
    }

    private HttpEntity<PatientTo> createRequestBody(PatientTo patientTo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(patientTo, headers);
    }

    @Nested
    class createsPatient {
        @Test
        void createPatient() {
            //given
            PatientTo patient = mockPatientTo(EMAIL_1);
            HttpEntity<PatientTo> request = createRequestBody(patient);
            //when
            ResponseEntity<PatientTo> response = restTemplate.postForEntity(CREATE_URL, request, PatientTo.class);
            //then
            PatientTo createdPatient = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(createdPatient).isNotNull();
            assertThat(createdPatient.id()).isNotNull();
            assertThat(createdPatient.email()).isEqualTo(patient.email());
            List<PatientEntity> patients = patientRepo.findAll();
            assertThat(patients).hasSize(1);
        }

        @ParameterizedTest
        @MethodSource("com.medic_manager.app.testdata.PatientTestdata#provideInvalidCreatePatientToList")
        void returnBadRequestWhenCreatePatientWithIncorrectTo(PatientTo patientTo) {
            //given
            HttpEntity<PatientTo> request = createRequestBody(patientTo);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseBody).isNotNull();
            List<PatientEntity> patients = patientRepo.findAll();
            assertThat(patients).isEmpty();
        }

        @Test
        void returnConflictWhenCreatePatientWithAlreadyExistingEmail() {
            //given
            PatientEntity existingPatient = mockPatientEntity(EMAIL_1);
            patientRepo.save(existingPatient);
            PatientTo newPatient = mockPatientTo(EMAIL_1);
            HttpEntity<PatientTo> request = createRequestBody(newPatient);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(responseBody).isNotNull();
            List<PatientEntity> patients = patientRepo.findAll();
            assertThat(patients).hasSize(1);
        }
    }

    @Nested
    class getsAllPatients {
        @Test
        void getAllPatients() {
            //given
            PatientEntity patient1 = mockPatientEntity(EMAIL_1);
            PatientEntity patient2 = mockPatientEntity(EMAIL_2);
            patientRepo.saveAll(List.of(patient1, patient2));
            //when
            ResponseEntity<List<PatientTo>> response = restTemplate.exchange(
                    GET_ALL_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            //then
            List<PatientTo> patients = response.getBody();
            PatientTo expectedPatient1 = patientMapper.toPatientTo(patient1);
            PatientTo expectedPatient2 = patientMapper.toPatientTo(patient2);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(patients).hasSize(2);
            assertThat(patients).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(expectedPatient1, expectedPatient2);
        }

        @Test
        void returnEmptyListWhenNoPatientsFound() {
            //given
            //when
            ResponseEntity<List<PatientTo>> response = restTemplate.exchange(
                    GET_ALL_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            //then
            List<PatientTo> patients = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(patients).isEmpty();
        }
    }

    @Nested
    class getsPatientById {
        @Test
        void getPatientById() {
            //given
            PatientEntity patient = mockPatientEntity(EMAIL_1);
            PatientEntity savedPatient = patientRepo.save(patient);
            //when
            ResponseEntity<PatientTo> response = restTemplate.getForEntity(GET_BY_ID_URL + savedPatient.getId(), PatientTo.class);
            //then
            PatientTo patientById = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            PatientTo expectedPatient = patientMapper.toPatientTo(savedPatient);
            assertThat(patientById).usingRecursiveComparison().isEqualTo(expectedPatient);
        }

        @Test
        void returnBadRequestWhenGetPatientByNullId() {
            //given
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.getForEntity(GET_BY_ID_URL + null, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseBody).isNotNull();
        }

        @Test
        void returnNotFoundWhenGetPatientByNotExistingId() {
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
    class updatesPatient {
        @Test
        void updatePatient() {
            //given
            PatientEntity patientEntity = mockPatientEntity(EMAIL_1);
            PatientEntity savedPatient = patientRepo.save(patientEntity);
            PatientTo updatePatientTo = mockPatientTo(savedPatient.getId(), EMAIL_2);
            HttpEntity<PatientTo> request = createRequestBody(updatePatientTo);
            //when
            ResponseEntity<PatientTo> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    PatientTo.class
            );
            //then
            PatientTo updatedPatient = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(updatedPatient).isNotNull();
            assertThat(updatedPatient.id()).isEqualTo(savedPatient.getId());
            assertThat(updatedPatient.email()).isNotEqualTo(savedPatient.getEmail());
            List<PatientEntity> patients = patientRepo.findAll();
            assertThat(patients).hasSize(1);
            assertThat(patients.get(0).getEmail()).isEqualTo(updatedPatient.email());
        }

        @ParameterizedTest
        @MethodSource("com.medic_manager.app.testdata.PatientTestdata#provideInvalidUpdatePatientToList")
        void returnBadRequestWhenUpdatePatientWithIncorrectTo(PatientTo patientTo) {
            //given
            HttpEntity<PatientTo> request = createRequestBody(patientTo);
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
            List<PatientEntity> patients = patientRepo.findAll();
            assertThat(patients).isEmpty();
        }

        @Test
        void returnConflictWhenUpdatePatientWithAlreadyExistingEmail() {
            //given
            PatientEntity patient = mockPatientEntity(EMAIL_1);
            PatientEntity existingPatient = patientRepo.save(patient);
            PatientTo updatePatient = mockPatientTo(existingPatient.getId(), EMAIL_1);
            HttpEntity<PatientTo> request = createRequestBody(updatePatient);
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
            List<PatientEntity> patients = patientRepo.findAll();
            assertThat(patients).hasSize(1);
        }

        @Test
        void returnBadRequestWhenUpdatePatientWithNotExistingId() {
            //given
            PatientEntity patient = mockPatientEntity(EMAIL_1);
            patientRepo.save(patient);
            PatientTo updatePatient = mockPatientTo(Long.MAX_VALUE, EMAIL_2);
            HttpEntity<PatientTo> request = createRequestBody(updatePatient);
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
            List<PatientEntity> patients = patientRepo.findAll();
            assertThat(patients).hasSize(1);
            assertThat(patients.get(0).getEmail()).isEqualTo(EMAIL_1);
        }
    }

    @Nested
    class deletesPatient {
        @Test
        void deletePatient() {
            //given
            PatientEntity patient1 = mockPatientEntity(EMAIL_1);
            PatientEntity patient2 = mockPatientEntity(EMAIL_2);
            PatientEntity savedPatient1 = patientRepo.save(patient1);
            PatientEntity savedPatient2 = patientRepo.save(patient2);
            //when
            ResponseEntity<Void> response = restTemplate.exchange(
                    DELETE_URL + savedPatient2.getId(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Optional<PatientEntity> deletedPatient = patientRepo.findById(savedPatient2.getId());
            assertThat(deletedPatient).isNotPresent();
            Optional<PatientEntity> leftPatient = patientRepo.findById(savedPatient1.getId());
            assertThat(leftPatient).isPresent();
        }

        @Test
        void returnBadRequestWhenDeletePatientByNullId() {
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
        void returnNoContentWhenDeletePatientByNotExistingId() {
            //given
            PatientEntity patient = mockPatientEntity(EMAIL_1);
            PatientEntity savedPatient = patientRepo.save(patient);
            //when
            ResponseEntity<Void> response = restTemplate.exchange(
                    DELETE_URL + Long.MAX_VALUE,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Optional<PatientEntity> notDeletedPatient = patientRepo.findById(savedPatient.getId());
            assertThat(notDeletedPatient).isPresent();
        }
    }
}
