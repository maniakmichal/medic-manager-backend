package com.medic_manager.app.testdata;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.enums.SpecializationEnum;
import com.medic_manager.app.tos.DoctorTo;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.boot.test.context.TestComponent;

import java.util.List;
import java.util.stream.Stream;

@TestComponent
public class DoctorTestdata {

    private static final String NAME = "Name";
    private static final String SURNAME = "Surname";
    private static final String IMAGE_URL = "";
    private static final String EMAIL = "email@example.com";

    public static DoctorEntity mockDoctorEntity(String email) {
        return mockDoctorEntity(null, email);
    }

    public static DoctorEntity mockDoctorEntity(Long id, String email) {
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setId(id);
        doctorEntity.setName(NAME);
        doctorEntity.setSurname(SURNAME);
        doctorEntity.setEmail(email);
        doctorEntity.setSpecializationEnums(List.of(SpecializationEnum.values()));
        doctorEntity.setImageUrl(IMAGE_URL);
        return doctorEntity;
    }

    public static DoctorTo mockDoctorTo(String email) {
        return mockDoctorTo(null, email);
    }

    public static DoctorTo mockDoctorTo(Long id, String email) {
        return new DoctorTo(
                id,
                NAME,
                SURNAME,
                email,
                List.of(SpecializationEnum.values()),
                IMAGE_URL
        );
    }

    public static Stream<Arguments> provideInvalidCreateDoctorToList() {
        return Stream.of(
                null,
                Arguments.of(new DoctorTo(null, null, SURNAME, EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, "", SURNAME, EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, " ", SURNAME, EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, null, EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, "", EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, " ", EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, SURNAME, null, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, SURNAME, "", List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, SURNAME, " ", List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, SURNAME, EMAIL, null, IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, SURNAME, EMAIL, List.of(), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, NAME, SURNAME, EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL))
        );
    }

    public static Stream<Arguments> provideInvalidUpdateDoctorToList() {
        return Stream.of(
                null,
                Arguments.of(new DoctorTo(1L, null, SURNAME, EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, "", SURNAME, EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, " ", SURNAME, EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, NAME, null, EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, NAME, "", EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, NAME, " ", EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, NAME, SURNAME, null, List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, NAME, SURNAME, "", List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, NAME, SURNAME, " ", List.of(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, NAME, SURNAME, EMAIL, null, IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, NAME, SURNAME, EMAIL, List.of(), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, SURNAME, EMAIL, List.of(SpecializationEnum.values()), IMAGE_URL))
        );
    }
}
