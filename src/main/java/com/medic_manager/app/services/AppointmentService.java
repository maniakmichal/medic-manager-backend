package com.medic_manager.app.services;

import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.exceptions.AppointmentCreationFailedBusinessException;
import com.medic_manager.app.exceptions.IncorrectDayOfWeekBusinessException;
import com.medic_manager.app.exceptions.IncorrectHourOrMinutesBusinessException;
import com.medic_manager.app.repositories.AppointmentRepo;
import com.medic_manager.app.tos.AppointmentTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.List;
import java.util.logging.Logger;

import static com.medic_manager.app.common.LoggerTextUtil.*;

@Service
@Transactional
public class AppointmentService {

    private static final String ERROR_DOCTOR_BUSY = "Doctor with ID: %d has got already appointment planned in the same date and time.";
    private static final String ERROR_PATIENT_BUSY = "Patient with ID: %d has got already appointment planned in the same date and time.";
    private final AppointmentRepo appointmentRepo;
    private final DoctorService doctorService;
    private final PatientService patientService;
    private final Logger logger = Logger.getLogger(AppointmentService.class.getName());

    public AppointmentService(AppointmentRepo appointmentRepo, DoctorService doctorService, PatientService patientService) {
        this.appointmentRepo = appointmentRepo;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public AppointmentEntity createAppointment(AppointmentTo appointmentTo) {
        validateCreateTo(appointmentTo);
        isValidDayOfWeek(appointmentTo.appointmentDayOfWeek());
        isValidHourAndMinutes(appointmentTo.appointmentHour(), appointmentTo.appointmentMinute());
        PatientEntity patientEntity = patientService.getPatientById(appointmentTo.patientId());
        DoctorEntity doctorEntity = doctorService.getDoctorById(appointmentTo.doctorId());
        isAppointmentValidToCreate(appointmentTo, doctorEntity, patientEntity);
        logger.info(() -> getCreateNewEntity(AppointmentEntity.class, appointmentTo));
        AppointmentEntity appointmentEntity = generateAppointment(appointmentTo);
        return appointmentRepo.save(appointmentEntity);
    }

    private AppointmentEntity generateAppointment(AppointmentTo appointmentTo) {
        DoctorEntity doctorEntity = doctorService.getDoctorById(appointmentTo.doctorId());
        PatientEntity patientEntity = patientService.getPatientById(appointmentTo.patientId());
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setAppointmentDate(appointmentTo.appointmentDate());
        appointmentEntity.setAppointmentHour(appointmentTo.appointmentHour());
        appointmentEntity.setAppointmentMinute(appointmentTo.appointmentMinute());
        appointmentEntity.setAppointmentDayOfWeek(appointmentTo.appointmentDayOfWeek());
        appointmentEntity.setAppointmentStatusEnum(appointmentTo.appointmentStatusEnum());
        appointmentEntity.setDoctorEntity(doctorEntity);
        appointmentEntity.setPatientEntity(patientEntity);
        return appointmentEntity;
    }

    private void validateCreateTo(AppointmentTo appointmentTo) throws IllegalArgumentException {
        if (isToInvalid(appointmentTo) || appointmentTo.id() != null) {
            logger.severe(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
        }
    }

    private boolean isToInvalid(AppointmentTo appointmentTo) {
        logger.info(getCheckingIfToInvalid());
        return appointmentTo == null
                || appointmentTo.appointmentDate() == null
                || appointmentTo.appointmentDayOfWeek() == null
                || appointmentTo.appointmentStatusEnum() == null
                || appointmentTo.doctorId() == null
                || appointmentTo.patientId() == null;
    }

    private void isValidDayOfWeek(DayOfWeek dayOfWeek) {
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            logger.severe(() -> gerErrorIncorrectDayOfWeek(dayOfWeek));
            throw new IncorrectDayOfWeekBusinessException(gerErrorIncorrectDayOfWeek(dayOfWeek));
        }
    }

    private void isValidHourAndMinutes(byte hour, byte minutes) {
        if (isHourInvalid(hour) || isMinuteInvalid(minutes)) {
            logger.severe(() -> getErrorIncorrectHourOrMinutes(hour, minutes));
            throw new IncorrectHourOrMinutesBusinessException(getErrorIncorrectHourOrMinutes(hour, minutes));
        }
    }

    private boolean isHourInvalid(byte hour) {
        return switch (hour) {
            case 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 -> false;
            default -> true;
        };
    }

    private boolean isMinuteInvalid(byte minutes) {
        return switch (minutes) {
            case 0, 15, 30, 45 -> false;
            default -> true;
        };
    }

    private void isAppointmentValidToCreate(AppointmentTo appointmentTo, DoctorEntity doctorEntity, PatientEntity patientEntity) {
        checkIfDoctorIsBusy(appointmentTo, doctorEntity);
        checkIfPatientIsBusy(appointmentTo, patientEntity);
    }

    private void checkIfDoctorIsBusy(AppointmentTo appointmentTo, DoctorEntity doctorEntity) {
        List<AppointmentEntity> doctorAppointments = appointmentRepo.findAllByDoctorEntityAndAppointmentDate(
                doctorEntity,
                appointmentTo.appointmentDate()
        );
        List<AppointmentEntity> filteredDoctorAppointments = doctorAppointments.stream()
                .filter(appointment -> appointment.getAppointmentHour() == appointmentTo.appointmentHour())
                .filter(appointment -> appointment.getAppointmentMinute() == appointmentTo.appointmentMinute())
                .toList();
        if (!filteredDoctorAppointments.isEmpty()) {
            String message = ERROR_DOCTOR_BUSY.formatted(appointmentTo.doctorId());
            logger.severe(() -> getErrorAppointmentCreationFailedDueTo() + message);
            throw new AppointmentCreationFailedBusinessException(getErrorAppointmentCreationFailedDueTo() + message);
        }
    }

    private void checkIfPatientIsBusy(AppointmentTo appointmentTo, PatientEntity patientEntity) {
        List<AppointmentEntity> patientAppointments = appointmentRepo.findAllByPatientEntityAndAppointmentDate(
                patientEntity,
                appointmentTo.appointmentDate()
        );
        List<AppointmentEntity> filteredPatientAppointments = patientAppointments.stream()
                .filter(appointment -> appointment.getAppointmentHour() == appointmentTo.appointmentHour())
                .filter(appointment -> appointment.getAppointmentMinute() == appointmentTo.appointmentMinute())
                .toList();
        if (!filteredPatientAppointments.isEmpty()) {
            String message = ERROR_PATIENT_BUSY.formatted(appointmentTo.patientId());
            logger.severe(() -> getErrorAppointmentCreationFailedDueTo() + message);
            throw new AppointmentCreationFailedBusinessException(getErrorAppointmentCreationFailedDueTo() + message);
        }
    }
}
