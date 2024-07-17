package com.medic_manager.app.testdata;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.enums.SpecializationEnum;

import java.util.Arrays;

public class DoctorTestdata {

    private static final String NAME = "Name";
    private static final String SURNAME = "Surname";
    private static final String IMAGE_URL = "";

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

    private static DoctorEntity mockDoctorEntity() {
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setName(NAME);
        doctorEntity.setSurname(SURNAME);
        doctorEntity.setSpecializationEnums(Arrays.asList(SpecializationEnum.values()));
        doctorEntity.setImageUrl(IMAGE_URL);
        return doctorEntity;
    }
}
