package com.medic_manager.app.mappers;

import com.medic_manager.app.entities.AppointmentEntity;
import com.medic_manager.app.tos.AppointmentTo;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentTo toAppointmentTo(AppointmentEntity entity) {
        return new AppointmentTo(
                entity.getId(),
                entity.getAppointmentDate(),
                entity.getAppointmentDayOfWeek(),
                entity.getAppointmentHour(),
                entity.getAppointmentMinute(),
                entity.getAppointmentStatusEnum(),
                entity.getDoctorEntity().getId(),
                entity.getPatientEntity().getId()
        );
    }
}
