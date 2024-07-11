package com.medic_manager.app.mappers;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.tos.DoctorTo;
import org.springframework.stereotype.Component;

@Component
public class DoctorMapper {

    public DoctorTo toDoctorTo(DoctorEntity entity) {
        return new DoctorTo(
                entity.getId(),
                entity.getName(),
                entity.getSurname(),
                entity.getEmail(),
                entity.getSpecializationEnums(),
                entity.getImageUrl()
        );
    }

}
