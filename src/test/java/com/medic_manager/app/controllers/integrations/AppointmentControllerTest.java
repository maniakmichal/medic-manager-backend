package com.medic_manager.app.controllers.integrations;

import com.medic_manager.app.IntegrationTestConfig;
import com.medic_manager.app.common.ErrorResponseUtil;
import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.enums.AppointmentStatusEnum;
import com.medic_manager.app.mappers.AppointmentMapper;
import com.medic_manager.app.repositories.AppointmentRepo;
import com.medic_manager.app.repositories.DoctorRepo;
import com.medic_manager.app.repositories.PatientRepo;
import com.medic_manager.app.testdata.AppointmentTestdata;
import com.medic_manager.app.testdata.DoctorTestdata;
import com.medic_manager.app.testdata.PatientTestdata;
import com.medic_manager.app.tos.AppointmentTo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTestConfig
class AppointmentControllerTest {

    private static final String CREATE_URL = "/com/medic-manager/app/create-appointment";
    private static final String GET_ALL_URL = "/com/medic-manager/app/appointments";
    private static final String GET_BY_ID_URL = "/com/medic-manager/app/appointment/";
    private static final String UPDATE_URL = "/com/medic-manager/app/update-appointment";
    private static final String DELETE_URL = "/com/medic-manager/app/delete-appointment/";
    private static final String EMAIL = "email@example.com";
    private static final String EMAIL2 = "email2@example.com";
    @Autowired
    private TestRestTemplate restTemplate;
    @Autowired
    private AppointmentRepo appointmentRepo;
    @Autowired
    private AppointmentMapper appointmentMapper;
    @Autowired
    private PatientRepo patientRepo;
    @Autowired
    private DoctorRepo doctorRepo;

    @AfterEach
    void cleanup() {
        appointmentRepo.deleteAll();
        patientRepo.deleteAll();
        doctorRepo.deleteAll();
    }

    private HttpEntity<AppointmentTo> createRequestBody(AppointmentTo appointmentTo) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(appointmentTo, headers);
    }

    @Nested
    class createsAppointment {
        @Test
        void createAppointment() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient = patientRepo.save(patient);
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            AppointmentTo appointment = AppointmentTestdata.mockAppointmentTo(null, savedDoctor.getId(), savedPatient.getId());
            HttpEntity<AppointmentTo> request = createRequestBody(appointment);
            //when
            ResponseEntity<AppointmentTo> response = restTemplate.postForEntity(CREATE_URL, request, AppointmentTo.class);
            //then
            AppointmentTo createdAppointment = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(createdAppointment).isNotNull();
            assertThat(createdAppointment.id()).isNotNull();
            assertThat(createdAppointment.appointmentDayOfWeek()).isEqualTo(appointment.appointmentDayOfWeek());
            assertThat(createdAppointment.doctorId()).isEqualTo(appointment.doctorId());
            assertThat(createdAppointment.patientId()).isEqualTo(appointment.patientId());
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).hasSize(1);
            Optional<PatientEntity> patientEntity = patientRepo.findByIdWithAppointments(savedPatient.getId());
            assertThat(patientEntity).isPresent();
            assertThat(patientEntity.get().getAppointmentEntityList()).hasSize(1);
            assertThat(patientEntity.get().getAppointmentEntityList().get(0))
                    .isNotNull()
                    .usingRecursiveComparison()
                    .ignoringFields("patientEntity")
                    .isEqualTo(appointments.get(0));
        }

        @ParameterizedTest
        @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidCreateAppointmentToList")
        void returnBadRequestWhenCreateAppointmentWithIncorrectTo(AppointmentTo appointmentTo) {
            //given
            HttpEntity<AppointmentTo> request = createRequestBody(appointmentTo);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseBody).isNotNull();
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).isEmpty();
        }

        @ParameterizedTest
        @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidBusinessDataForCreate")
        void returnForbiddenWhenCreateAppointmentWithIncorrectBusinessData(AppointmentTo appointmentTo) {
            //given
            HttpEntity<AppointmentTo> request = createRequestBody(appointmentTo);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseBody).isNotNull();
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).isEmpty();
        }

        @Test
        void returnNotFoundWhenCreateAppointmentWithPatientNotFound() {
            //given
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            doctorRepo.save(doctor);
            AppointmentTo appointment = AppointmentTestdata.mockAppointmentTo();
            HttpEntity<AppointmentTo> request = createRequestBody(appointment);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseBody).isNotNull();
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).isEmpty();
        }

        @Test
        void returnNotFoundWhenCreateAppointmentWithDoctorNotFound() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            patientRepo.save(patient);
            AppointmentTo appointment = AppointmentTestdata.mockAppointmentTo();
            HttpEntity<AppointmentTo> request = createRequestBody(appointment);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(responseBody).isNotNull();
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).isEmpty();
        }

        @Test
        void returnForbiddenWhenCreateAppointmentWithBusyPatient() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient = patientRepo.save(patient);
            DoctorEntity doctor1 = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor1 = doctorRepo.save(doctor1);
            DoctorEntity doctor2 = DoctorTestdata.mockDoctorEntity(EMAIL2);
            DoctorEntity savedDoctor2 = doctorRepo.save(doctor2);
            AppointmentEntity appointment1 = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor1, savedPatient);
            appointmentRepo.save(appointment1);
            AppointmentTo appointment = AppointmentTestdata.mockAppointmentTo(null, savedDoctor2.getId(), savedPatient.getId());
            HttpEntity<AppointmentTo> request = createRequestBody(appointment);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseBody).isNotNull();
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).hasSize(1);
        }

        @Test
        void returnForbiddenWhenCreateAppointmentWithBusyDoctor() {
            //given
            PatientEntity patient1 = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient1 = patientRepo.save(patient1);
            PatientEntity patient2 = PatientTestdata.mockPatientEntity(EMAIL2);
            PatientEntity savedPatient2 = patientRepo.save(patient2);
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            AppointmentEntity appointment1 = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor, savedPatient1);
            appointmentRepo.save(appointment1);
            AppointmentTo appointment = AppointmentTestdata.mockAppointmentTo(null, savedDoctor.getId(), savedPatient2.getId());
            HttpEntity<AppointmentTo> request = createRequestBody(appointment);
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.postForEntity(CREATE_URL, request, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
            assertThat(responseBody).isNotNull();
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).hasSize(1);
        }
    }

    @Nested
    class getsAllAppointments {
        @Test
        void getAllAppointments() {
            //given
            PatientEntity patient1 = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient1 = patientRepo.save(patient1);
            DoctorEntity doctor1 = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor1 = doctorRepo.save(doctor1);
            PatientEntity patient2 = PatientTestdata.mockPatientEntity(EMAIL2);
            PatientEntity savedPatient2 = patientRepo.save(patient2);
            DoctorEntity doctor2 = DoctorTestdata.mockDoctorEntity(EMAIL2);
            DoctorEntity savedDoctor2 = doctorRepo.save(doctor2);
            AppointmentEntity appointment1 = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor1, savedPatient1);
            AppointmentEntity appointment2 = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor2, savedPatient2);
            appointmentRepo.saveAll(List.of(appointment1, appointment2));
            //when
            ResponseEntity<List<AppointmentTo>> response = restTemplate.exchange(
                    GET_ALL_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            //then
            List<AppointmentTo> appointments = response.getBody();
            AppointmentTo expectedAppointment1 = appointmentMapper.toAppointmentTo(appointment1);
            AppointmentTo expectedAppointment2 = appointmentMapper.toAppointmentTo(appointment2);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(appointments).hasSize(2);
            assertThat(appointments).usingRecursiveFieldByFieldElementComparator().containsExactlyInAnyOrder(expectedAppointment1, expectedAppointment2);
        }

        @Test
        void returnEmptyListWhenNoAppointmentsFound() {
            //given
            //when
            ResponseEntity<List<AppointmentTo>> response = restTemplate.exchange(
                    GET_ALL_URL,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            //then
            List<AppointmentTo> appointments = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(appointments).isEmpty();
        }
    }

    @Nested
    class getsAppointmentById {
        @Test
        void getAppointmentById() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient = patientRepo.save(patient);
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor, savedPatient);
            AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
            //when
            ResponseEntity<AppointmentTo> response = restTemplate.getForEntity(GET_BY_ID_URL + savedAppointment.getId(), AppointmentTo.class);
            //then
            AppointmentTo appointmentById = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            AppointmentTo expectedAppointment = appointmentMapper.toAppointmentTo(savedAppointment);
            assertThat(appointmentById).usingRecursiveComparison().isEqualTo(expectedAppointment);
        }

        @Test
        void returnBadRequestWhenGetAppointmentByNullId() {
            //given
            //when
            ResponseEntity<ErrorResponseUtil> response = restTemplate.getForEntity(GET_BY_ID_URL + null, ErrorResponseUtil.class);
            //then
            ErrorResponseUtil responseBody = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(responseBody).isNotNull();
        }

        @Test
        void returnNotFoundWhenGetAppointmentByNotExistingId() {
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
    class updatesAppointment {
        @Test
        void updateAppointment() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient = patientRepo.save(patient);
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor, savedPatient);
            AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
            AppointmentTo updateAppointmentTo = AppointmentTestdata.mockAppointmentTo(
                    savedAppointment.getId(),
                    LocalDate.of(2024, 12, 18),
                    DayOfWeek.FRIDAY,
                    (byte) 10,
                    (byte) 45,
                    AppointmentStatusEnum.IN_PROGRESS,
                    savedDoctor.getId(),
                    savedPatient.getId()
            );
            HttpEntity<AppointmentTo> request = createRequestBody(updateAppointmentTo);
            //when
            ResponseEntity<AppointmentTo> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    AppointmentTo.class
            );
            //then
            AppointmentTo updatedAppointment = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(updatedAppointment).isNotNull();
            assertThat(updatedAppointment.id()).isEqualTo(savedAppointment.getId());
            assertThat(updatedAppointment.appointmentDayOfWeek()).isEqualTo(updateAppointmentTo.appointmentDayOfWeek());
            assertThat(updatedAppointment.appointmentDayOfWeek()).isNotEqualTo(savedAppointment.getAppointmentDayOfWeek());
            assertThat(updatedAppointment.appointmentStatusEnum()).isEqualTo(updateAppointmentTo.appointmentStatusEnum());
            assertThat(updatedAppointment.appointmentStatusEnum()).isNotEqualTo(savedAppointment.getAppointmentStatusEnum());
            assertThat(updatedAppointment.appointmentDate()).isEqualTo(updateAppointmentTo.appointmentDate());
            assertThat(updatedAppointment.appointmentDate()).isNotEqualTo(savedAppointment.getAppointmentDate());
            assertThat(updatedAppointment.appointmentHour()).isEqualTo(updateAppointmentTo.appointmentHour());
            assertThat(updatedAppointment.appointmentHour()).isNotEqualTo(savedAppointment.getAppointmentHour());
            assertThat(updatedAppointment.appointmentMinute()).isEqualTo(updateAppointmentTo.appointmentMinute());
            assertThat(updatedAppointment.appointmentMinute()).isNotEqualTo(savedAppointment.getAppointmentMinute());
            assertThat(updatedAppointment.doctorId()).isEqualTo(savedDoctor.getId());
            assertThat(updatedAppointment.patientId()).isEqualTo(savedPatient.getId());
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).hasSize(1);
        }

        @Test
        void updateAppointmentWithNewPatient() {
            //given
            PatientEntity patient1 = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient1 = patientRepo.save(patient1);
            PatientEntity patient2 = PatientTestdata.mockPatientEntity(EMAIL2);
            PatientEntity savedPatient2 = patientRepo.save(patient2);
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor, savedPatient1);
            AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
            AppointmentTo updateAppointmentTo = AppointmentTestdata.mockAppointmentTo(
                    savedAppointment.getId(),
                    savedDoctor.getId(),
                    savedPatient2.getId()
            );
            HttpEntity<AppointmentTo> request = createRequestBody(updateAppointmentTo);
            //when
            ResponseEntity<AppointmentTo> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    AppointmentTo.class
            );
            //then
            AppointmentTo updatedAppointment = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(updatedAppointment).isNotNull();
            assertThat(updatedAppointment.id()).isEqualTo(savedAppointment.getId());
            assertThat(updatedAppointment.doctorId()).isEqualTo(savedDoctor.getId());
            assertThat(updatedAppointment.patientId()).isEqualTo(savedPatient2.getId());
            assertThat(updatedAppointment.patientId()).isNotEqualTo(savedPatient1.getId());
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).hasSize(1);
            Optional<PatientEntity> oldPatient = patientRepo.findByIdWithAppointments(savedPatient1.getId());
            assertThat(oldPatient).isPresent();
            assertThat(oldPatient.get().getAppointmentEntityList()).isEmpty();
            Optional<PatientEntity> expectedPatient = patientRepo.findByIdWithAppointments(savedPatient2.getId());
            assertThat(expectedPatient).isPresent();
            assertThat(expectedPatient.get().getAppointmentEntityList()).hasSize(1);
            assertThat(expectedPatient.get().getAppointmentEntityList().get(0))
                    .isNotNull()
                    .usingRecursiveComparison()
                    .ignoringFields("patientEntity")
                    .isEqualTo(appointments.get(0));
        }

        @Test
        void updateAppointmentWithNewDoctor() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient = patientRepo.save(patient);
            DoctorEntity doctor1 = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor1 = doctorRepo.save(doctor1);
            DoctorEntity doctor2 = DoctorTestdata.mockDoctorEntity(EMAIL2);
            DoctorEntity savedDoctor2 = doctorRepo.save(doctor2);
            AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor1, savedPatient);
            AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
            AppointmentTo updateAppointmentTo = AppointmentTestdata.mockAppointmentTo(
                    savedAppointment.getId(),
                    savedDoctor2.getId(),
                    savedPatient.getId()
            );
            HttpEntity<AppointmentTo> request = createRequestBody(updateAppointmentTo);
            //when
            ResponseEntity<AppointmentTo> response = restTemplate.exchange(
                    UPDATE_URL,
                    HttpMethod.PUT,
                    request,
                    AppointmentTo.class
            );
            //then
            AppointmentTo updatedAppointment = response.getBody();
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(updatedAppointment).isNotNull();
            assertThat(updatedAppointment.id()).isEqualTo(savedAppointment.getId());
            assertThat(updatedAppointment.doctorId()).isEqualTo(savedDoctor2.getId());
            assertThat(updatedAppointment.doctorId()).isNotEqualTo(savedDoctor1.getId());
            assertThat(updatedAppointment.patientId()).isEqualTo(savedPatient.getId());
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).hasSize(1);
        }

        @ParameterizedTest
        @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidUpdateAppointmentToList")
        void returnBadRequestWhenUpdateAppointmentWithIncorrectTo(AppointmentTo appointmentTo) {
            //given
            HttpEntity<AppointmentTo> request = createRequestBody(appointmentTo);
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
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).isEmpty();
        }

        @ParameterizedTest
        @MethodSource("com.medic_manager.app.testdata.AppointmentTestdata#provideInvalidBusinessDataForUpdate")
        void returnForbiddenWhenUpdateAppointmentWithIncorrectBusinessData(AppointmentTo appointmentTo) {
            //given
            HttpEntity<AppointmentTo> request = createRequestBody(appointmentTo);
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
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).isEmpty();
        }

        @Test
        void returnNotFoundWhenUpdateAppointmentWithPatientNotFound() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient = patientRepo.save(patient);
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor, savedPatient);
            AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
            AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo(savedAppointment.getId(), savedDoctor.getId(), Long.MAX_VALUE);
            HttpEntity<AppointmentTo> request = createRequestBody(appointmentTo);
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
        void returnNotFoundWhenCreateAppointmentWithDoctorNotFound() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient = patientRepo.save(patient);
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor, savedPatient);
            AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
            AppointmentTo appointmentTo = AppointmentTestdata.mockAppointmentTo(savedAppointment.getId(), Long.MAX_VALUE, savedPatient.getId());
            HttpEntity<AppointmentTo> request = createRequestBody(appointmentTo);
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
        void returnForbiddenWhenUpdateAppointmentWithBusyPatient() {
            //given
            PatientEntity patient1 = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient1 = patientRepo.save(patient1);
            PatientEntity patient2 = PatientTestdata.mockPatientEntity(EMAIL2);
            PatientEntity savedPatient2 = patientRepo.save(patient2);
            DoctorEntity doctor1 = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor1 = doctorRepo.save(doctor1);
            DoctorEntity doctor2 = DoctorTestdata.mockDoctorEntity(EMAIL2);
            DoctorEntity savedDoctor2 = doctorRepo.save(doctor2);
            AppointmentEntity appointmentEntity1 = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor1, savedPatient1);
            AppointmentEntity savedAppointment1 = appointmentRepo.save(appointmentEntity1);
            AppointmentEntity appointmentEntity2 = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor2, savedPatient2);
            appointmentRepo.save(appointmentEntity2);
            AppointmentTo updateAppointmentTo = AppointmentTestdata.mockAppointmentTo(
                    savedAppointment1.getId(),
                    savedDoctor1.getId(),
                    savedPatient2.getId()
            );
            HttpEntity<AppointmentTo> request = createRequestBody(updateAppointmentTo);
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
        void returnForbiddenWhenUpdateAppointmentWithBusyDoctor() {
            //given
            PatientEntity patient1 = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient1 = patientRepo.save(patient1);
            PatientEntity patient2 = PatientTestdata.mockPatientEntity(EMAIL2);
            PatientEntity savedPatient2 = patientRepo.save(patient2);
            DoctorEntity doctor1 = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor1 = doctorRepo.save(doctor1);
            DoctorEntity doctor2 = DoctorTestdata.mockDoctorEntity(EMAIL2);
            DoctorEntity savedDoctor2 = doctorRepo.save(doctor2);
            AppointmentEntity appointmentEntity1 = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor1, savedPatient1);
            AppointmentEntity savedAppointment1 = appointmentRepo.save(appointmentEntity1);
            AppointmentEntity appointmentEntity2 = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor2, savedPatient2);
            appointmentRepo.save(appointmentEntity2);
            AppointmentTo updateAppointmentTo = AppointmentTestdata.mockAppointmentTo(
                    savedAppointment1.getId(),
                    savedDoctor2.getId(),
                    savedPatient1.getId()
            );
            HttpEntity<AppointmentTo> request = createRequestBody(updateAppointmentTo);
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
    class deletesAppointment {
        @Test
        void deleteAppointment() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient = patientRepo.save(patient);
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor, savedPatient);
            AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
            //when
            ResponseEntity<Void> response = restTemplate.exchange(
                    DELETE_URL + savedAppointment.getId(),
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Optional<AppointmentEntity> deletedAppointment = appointmentRepo.findById(savedAppointment.getId());
            assertThat(deletedAppointment).isNotPresent();
            List<AppointmentEntity> allAppointments = appointmentRepo.findAll();
            assertThat(allAppointments).isEmpty();
        }

        @Test
        void returnBadRequestWhenDeleteAppointmentByNullId() {
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
        void returnNoContentWhenDeleteAppointmentByNotExistingId() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient = patientRepo.save(patient);
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor, savedPatient);
            AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
            //when
            ResponseEntity<Void> response = restTemplate.exchange(
                    DELETE_URL + Long.MAX_VALUE,
                    HttpMethod.DELETE,
                    null,
                    Void.class
            );
            //then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
            Optional<AppointmentEntity> notDeletedAppointment = appointmentRepo.findById(savedAppointment.getId());
            assertThat(notDeletedAppointment).isPresent();
            List<AppointmentEntity> allAppointments = appointmentRepo.findAll();
            assertThat(allAppointments).hasSize(1);
        }
    }

    @Nested
    class patientAndAppointmentCascadeTest {
        @Test
        void deleteAppointmentByCascadeWhenDeletePatient() {
            //given
            PatientEntity patient = PatientTestdata.mockPatientEntity(EMAIL);
            PatientEntity savedPatient = patientRepo.save(patient);
            DoctorEntity doctor = DoctorTestdata.mockDoctorEntity(EMAIL);
            DoctorEntity savedDoctor = doctorRepo.save(doctor);
            AppointmentEntity appointmentEntity = AppointmentTestdata.mockAppointmentEntity(null, savedDoctor, savedPatient);
            AppointmentEntity savedAppointment = appointmentRepo.save(appointmentEntity);
            //then
            assertThat(savedAppointment).isNotNull();
            Optional<PatientEntity> patientEntity = patientRepo.findByIdWithAppointments(savedPatient.getId());
            assertThat(patientEntity).isPresent();
            assertThat(patientEntity.get().getAppointmentEntityList()).hasSize(1);
            assertThat(patientEntity.get().getAppointmentEntityList().get(0))
                    .isNotNull()
                    .usingRecursiveComparison()
                    .ignoringFields("patientEntity", "doctorEntity", "createdAt", "modifiedAt")
                    .isEqualTo(savedAppointment);
            //when
            patientRepo.deleteById(savedPatient.getId());
            //then
            List<PatientEntity> patients = patientRepo.findAll();
            assertThat(patients).isEmpty();
            List<AppointmentEntity> appointments = appointmentRepo.findAll();
            assertThat(appointments).isEmpty();
            List<DoctorEntity> doctors = doctorRepo.findAll();
            assertThat(doctors).hasSize(1);
        }
    }
}
