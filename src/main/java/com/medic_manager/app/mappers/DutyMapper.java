package com.medic_manager.app.mappers;

import com.medic_manager.app.entities.DutyEntity;
import com.medic_manager.app.tos.DutyTo;
import org.springframework.stereotype.Component;

@Component
public class DutyMapper {

    public DutyTo toDutyTo(DutyEntity dutyEntity) {
        return new DutyTo(
                dutyEntity.getId(),
                dutyEntity.getDoctorEntity().getId(),
                dutyEntity.getStartDate(),
                dutyEntity.getEndDate()
        );
    }
}
