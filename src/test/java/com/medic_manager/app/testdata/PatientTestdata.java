package com.medic_manager.app.testdata;

import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.enums.GenderEnum;
import com.medic_manager.app.tos.PatientTo;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.boot.test.context.TestComponent;

import java.time.LocalDate;
import java.util.stream.Stream;

@TestComponent
public class PatientTestdata {

    private static final String NAME = "Name";
    private static final String SURNAME = "Surname";
    private static final String EMAIL = "email@example.com";
    private static final LocalDate BIRTHDATE = LocalDate.of(2024, 1, 15);
    private static final GenderEnum GENDER_ENUM = GenderEnum.UNKNOWN;

    public static PatientEntity mockPatientEntity(String email) {
        return mockPatientEntity(null, email);
    }

    public static PatientEntity mockPatientEntity(Long id, String email) {
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setId(id);
        patientEntity.setName(NAME);
        patientEntity.setSurname(SURNAME);
        patientEntity.setEmail(email);
        patientEntity.setBirthdate(BIRTHDATE);
        patientEntity.setGenderEnum(GENDER_ENUM);
        return patientEntity;
    }

    public static PatientTo mockPatientTo(String email) {
        return mockPatientTo(null, email);
    }

    public static PatientTo mockPatientTo(Long id, String email) {
        return new PatientTo(
                id,
                NAME,
                SURNAME,
                email,
                BIRTHDATE,
                GENDER_ENUM
        );
    }

    public static Stream<Arguments> provideInvalidCreatePatientToList() {
        return Stream.of(
                null,
                Arguments.of(new PatientTo(null, null, SURNAME, EMAIL, BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(null, "", SURNAME, EMAIL, BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(null, NAME, null, EMAIL, BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(null, NAME, "", EMAIL, BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(null, NAME, SURNAME, null, BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(null, NAME, SURNAME, "", BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(null, NAME, SURNAME, EMAIL, null, GENDER_ENUM)),
                Arguments.of(new PatientTo(null, NAME, SURNAME, EMAIL, BIRTHDATE, null)),
                Arguments.of(new PatientTo(1L, NAME, SURNAME, EMAIL, BIRTHDATE, GENDER_ENUM))
        );
    }

    public static Stream<Arguments> provideInvalidUpdatePatientToList() {
        return Stream.of(
                null,
                Arguments.of(new PatientTo(1L, null, SURNAME, EMAIL, BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(1L, "", SURNAME, EMAIL, BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(1L, NAME, null, EMAIL, BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(1L, NAME, "", EMAIL, BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(1L, NAME, SURNAME, null, BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(1L, NAME, SURNAME, "", BIRTHDATE, GENDER_ENUM)),
                Arguments.of(new PatientTo(1L, NAME, SURNAME, EMAIL, null, GENDER_ENUM)),
                Arguments.of(new PatientTo(1L, NAME, SURNAME, EMAIL, BIRTHDATE, null)),
                Arguments.of(new PatientTo(null, NAME, SURNAME, EMAIL, BIRTHDATE, GENDER_ENUM))
        );
    }
}
