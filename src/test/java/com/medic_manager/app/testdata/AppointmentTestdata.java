package com.medic_manager.app.testdata;

import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.enums.AppointmentStatusEnum;
import com.medic_manager.app.tos.AppointmentTo;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.boot.test.context.TestComponent;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@TestComponent
public class AppointmentTestdata {
    private static final LocalDate APPOINTMENT_DATE = LocalDate.of(2024, 10, 8);
    private static final byte APPOINTMENT_HOUR = 15;
    private static final byte APPOINTMENT_MINUTE = 30;
    private static final Long ID = 1L;
    private static final String EMAIL = "email@example.com";

    public static AppointmentEntity mockAppointmentEntity() {
        return mockAppointmentEntity(null);
    }

    public static AppointmentEntity mockAppointmentEntity(Long id) {
        return mockAppointmentEntity(id, DoctorTestdata.mockDoctorEntity(ID, EMAIL), PatientTestdata.mockPatientEntity(ID, EMAIL));
    }
    
    public static AppointmentEntity mockAppointmentEntity(Long id, DoctorEntity doctorEntity, PatientEntity patientEntity) {
        AppointmentEntity appointmentEntity = new AppointmentEntity();
        appointmentEntity.setId(id);
        appointmentEntity.setAppointmentDate(APPOINTMENT_DATE);
        appointmentEntity.setAppointmentDayOfWeek(DayOfWeek.TUESDAY);
        appointmentEntity.setAppointmentStatusEnum(AppointmentStatusEnum.PENDING);
        appointmentEntity.setAppointmentHour(APPOINTMENT_HOUR);
        appointmentEntity.setAppointmentMinute(APPOINTMENT_MINUTE);
        appointmentEntity.setDoctorEntity(doctorEntity);
        appointmentEntity.setPatientEntity(patientEntity);
        return appointmentEntity;
    }

    public static AppointmentTo mockAppointmentTo() {
        return mockAppointmentTo(null);
    }

    public static AppointmentTo mockAppointmentTo(Long id) {
        return mockAppointmentTo(id, ID, ID);
    }

    public static AppointmentTo mockAppointmentTo(Long id, Long doctorId, Long patientId) {
        return mockAppointmentTo(
                id,
                APPOINTMENT_DATE,
                DayOfWeek.TUESDAY,
                APPOINTMENT_HOUR,
                APPOINTMENT_MINUTE,
                AppointmentStatusEnum.PENDING,
                doctorId,
                patientId
        );
    }

    public static AppointmentTo mockAppointmentTo(
            Long id,
            LocalDate appointmentDate,
            DayOfWeek dayOfWeek,
            byte hour,
            byte minute,
            AppointmentStatusEnum appointmentStatusEnum,
            Long doctorId,
            Long patientId
    ) {
        return new AppointmentTo(
                id,
                appointmentDate,
                dayOfWeek,
                hour,
                minute,
                appointmentStatusEnum,
                doctorId,
                patientId
        );
    }

    public static Stream<Arguments> provideInvalidCreateAppointmentToList() {
        return Stream.of(
                null,
                Arguments.of(new AppointmentTo(ID, APPOINTMENT_DATE, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID)),
                Arguments.of(new AppointmentTo(null, null, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID)),
                Arguments.of(new AppointmentTo(null, APPOINTMENT_DATE, null, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID)),
                Arguments.of(new AppointmentTo(null, APPOINTMENT_DATE, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, null, ID, ID)),
                Arguments.of(new AppointmentTo(null, APPOINTMENT_DATE, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, null, ID)),
                Arguments.of(new AppointmentTo(null, APPOINTMENT_DATE, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, null))
        );
    }

    public static Stream<Arguments> provideInvalidUpdateAppointmentToList() {
        return Stream.of(
                null,
                Arguments.of(new AppointmentTo(null, APPOINTMENT_DATE, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID)),
                Arguments.of(new AppointmentTo(ID, null, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID)),
                Arguments.of(new AppointmentTo(ID, APPOINTMENT_DATE, null, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID)),
                Arguments.of(new AppointmentTo(ID, APPOINTMENT_DATE, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, null, ID, ID)),
                Arguments.of(new AppointmentTo(ID, APPOINTMENT_DATE, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, null, ID)),
                Arguments.of(new AppointmentTo(ID, APPOINTMENT_DATE, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, null))
        );
    }

    public static Stream<Arguments> provideInvalidDayOfWeekList() {
        return Stream.of(
                Arguments.of(new AppointmentTo(null, APPOINTMENT_DATE, DayOfWeek.SATURDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID)),
                Arguments.of(new AppointmentTo(null, APPOINTMENT_DATE, DayOfWeek.SUNDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID))
        );
    }

    public static Stream<Arguments> provideInvalidDayOfWeekListForUpdate() {
        return Stream.of(
                Arguments.of(new AppointmentTo(ID, APPOINTMENT_DATE, DayOfWeek.SATURDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID)),
                Arguments.of(new AppointmentTo(ID, APPOINTMENT_DATE, DayOfWeek.SUNDAY, APPOINTMENT_HOUR, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID))
        );
    }

    public static Stream<Arguments> provideInvalidHourList() {
        return Stream.of(
                        IntStream.rangeClosed(0, 7).mapToObj(hour -> (byte) hour),
                        IntStream.rangeClosed(18, 23).mapToObj(hour -> (byte) hour)
                )
                .flatMap(stream -> stream)
                .map(hour -> Arguments.of(new AppointmentTo(null, APPOINTMENT_DATE, DayOfWeek.TUESDAY, hour, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID)));
    }

    public static Stream<Arguments> provideInvalidHourListForUpdate() {
        return Stream.of(
                        IntStream.rangeClosed(0, 7).mapToObj(hour -> (byte) hour),
                        IntStream.rangeClosed(18, 23).mapToObj(hour -> (byte) hour)
                )
                .flatMap(stream -> stream)
                .map(hour -> Arguments.of(new AppointmentTo(ID, APPOINTMENT_DATE, DayOfWeek.TUESDAY, hour, APPOINTMENT_MINUTE, AppointmentStatusEnum.PENDING, ID, ID)));
    }

    public static Stream<Arguments> provideInvalidMinuteList() {
        return Stream.of(
                        IntStream.rangeClosed(1, 60)
                                .mapToObj(minute -> (byte) minute)
                                .filter(minute -> minute != 15 && minute != 30 && minute != 45)
                )
                .flatMap(stream -> stream)
                .map(minute -> Arguments.of(new AppointmentTo(null, APPOINTMENT_DATE, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, minute, AppointmentStatusEnum.PENDING, ID, ID)));
    }

    public static Stream<Arguments> provideInvalidMinuteListForUpdate() {
        return Stream.of(
                        IntStream.rangeClosed(1, 60)
                                .mapToObj(minute -> (byte) minute)
                                .filter(minute -> minute != 15 && minute != 30 && minute != 45)
                )
                .flatMap(stream -> stream)
                .map(minute -> Arguments.of(new AppointmentTo(ID, APPOINTMENT_DATE, DayOfWeek.TUESDAY, APPOINTMENT_HOUR, minute, AppointmentStatusEnum.PENDING, ID, ID)));
    }

    public static Stream<Arguments> provideInvalidBusinessDataForCreate() {
        return Stream.of(
                provideInvalidHourList(),
                provideInvalidDayOfWeekList(),
                provideInvalidMinuteList()
        ).flatMap(stream -> stream);
    }

    public static Stream<Arguments> provideInvalidBusinessDataForUpdate() {
        return Stream.of(
                provideInvalidHourListForUpdate(),
                provideInvalidDayOfWeekListForUpdate(),
                provideInvalidMinuteListForUpdate()
        ).flatMap(stream -> stream);
    }
}
