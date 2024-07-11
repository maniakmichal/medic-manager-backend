package com.medic_manager.app.mappers;

import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.tos.PatientTo;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public PatientTo toPatientTo(PatientEntity entity) {
        return new PatientTo(
                entity.getName(),
                entity.getSurname(),
                entity.getEmail(),
                entity.getBirthdate(),
                entity.getGenderEnum()
        );
    }

}
