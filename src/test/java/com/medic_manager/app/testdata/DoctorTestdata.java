package com.medic_manager.app.testdata;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.enums.SpecializationEnum;
import com.medic_manager.app.tos.DoctorTo;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.boot.test.context.TestComponent;

import java.util.Arrays;
import java.util.stream.Stream;

@TestComponent
public class DoctorTestdata {

    private static final String NAME = "Name";
    private static final String SURNAME = "Surname";
    private static final String IMAGE_URL = "";
    private static final String EMAIL = "email@example.com";

    public static DoctorEntity mockDoctorEntityWithIdAndEmail(Long id, String email) {
        DoctorEntity doctorEntity = mockDoctorEntityWithEmail(email);
        doctorEntity.setId(id);
        return doctorEntity;
    }

    public static DoctorEntity mockDoctorEntityWithEmail(String email) {
        DoctorEntity doctorEntity = mockDoctorEntity();
        doctorEntity.setEmail(email);
        return doctorEntity;
    }

    public static DoctorTo mockCreateDoctorTo(String email) {
        return new DoctorTo(
                null,
                NAME,
                SURNAME,
                email,
                Arrays.asList(SpecializationEnum.values()),
                IMAGE_URL
        );
    }

    public static Stream<Arguments> provideInvalidDoctorToList() {
        return Stream.of(
                Arguments.of(new DoctorTo(null, null, SURNAME, EMAIL, Arrays.asList(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, "", SURNAME, EMAIL, Arrays.asList(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, null, EMAIL, Arrays.asList(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, "", EMAIL, Arrays.asList(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, SURNAME, null, Arrays.asList(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, SURNAME, "", Arrays.asList(SpecializationEnum.values()), IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, SURNAME, EMAIL, null, IMAGE_URL)),
                Arguments.of(new DoctorTo(null, NAME, SURNAME, EMAIL, Arrays.asList(), IMAGE_URL)),
                Arguments.of(new DoctorTo(1L, NAME, SURNAME, EMAIL, Arrays.asList(SpecializationEnum.values()), IMAGE_URL))
        );
    }

    private static DoctorEntity mockDoctorEntity() {
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setName(NAME);
        doctorEntity.setSurname(SURNAME);
        doctorEntity.setSpecializationEnums(Arrays.asList(SpecializationEnum.values()));
        doctorEntity.setImageUrl(IMAGE_URL);
        return doctorEntity;
    }
}
