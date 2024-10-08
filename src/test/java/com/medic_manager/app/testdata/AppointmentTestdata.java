package com.medic_manager.app.testdata;

import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.enums.AppointmentStatusEnum;
import org.springframework.boot.test.context.TestComponent;

import java.time.DayOfWeek;
import java.time.LocalDate;

@TestComponent
public class AppointmentTestdata {
    private static final LocalDate APPOINTMENT_DATE = LocalDate.of(2024, 10, 8);
    private static final byte APPOINTMENT_HOUR = 15;
    private static final byte APPOINTMENT_MINUTE = 30;
    private static final Long ID = 1L;
    private static final String EMAIL = "email@example.com";

    public static AppointmentEntity mockAppointmentEntity() {
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setId(null);
        appointmentEntity.setAppointmentDate(APPOINTMENT_DATE);
        appointmentEntity.setAppointmentDayOfWeek(DayOfWeek.TUESDAY);
        appointmentEntity.setAppointmentStatusEnum(AppointmentStatusEnum.PENDING);
        appointmentEntity.setAppointmentHour(APPOINTMENT_HOUR);
        appointmentEntity.setAppointmentMinute(APPOINTMENT_MINUTE);
        appointmentEntity.setDoctorEntity(DoctorTestdata.mockDoctorEntity(ID, EMAIL));
        appointmentEntity.setPatientEntity(PatientTestdata.mockPatientEntity(ID, EMAIL));
        return appointmentEntity;
    }
}
