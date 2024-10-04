package com.medic_manager.app.services;

import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.exceptions.IncorrectDayOfWeekBusinessException;
import com.medic_manager.app.repositories.AppointmentRepo;
import com.medic_manager.app.tos.AppointmentTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.util.logging.Logger;

import static com.medic_manager.app.common.LoggerTextUtil.*;

@Service
@Transactional
public class AppointmentService {

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
        //TODO implement business logic to check if already existing
        isValidDayOfWeek(appointmentTo.appointmentDayOfWeek());
        isValidHourAndMinutes(appointmentTo.appointmentHour(), appointmentTo.appointmentMinute());
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
            logger.severe(gerErrorIncorrectDayOfWeek(dayOfWeek));
            throw new IncorrectDayOfWeekBusinessException(gerErrorIncorrectDayOfWeek(dayOfWeek));
        }
    }

    private void isValidHourAndMinutes(byte hour, byte minutes) {
        if (isHourInvalid(hour) || isMinuteInvalid(minutes)) {
            logger.severe(getErrorIncorrectHourOrMinutes(hour, minutes));
            //TODO prepare business exception
            throw new RuntimeException(getErrorIncorrectHourOrMinutes(hour, minutes));
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
}
