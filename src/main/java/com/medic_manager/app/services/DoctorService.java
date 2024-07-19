package com.medic_manager.app.services;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.repositories.DoctorRepo;
import com.medic_manager.app.tos.DoctorTo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
@Transactional
public class DoctorService {

    private static final String LIST_ALL_DOCTORS = "List all doctors.";

    private final DoctorRepo doctorRepo;
    private final Logger logger = Logger.getLogger(DoctorService.class.getName());

    public DoctorService(final DoctorRepo doctorRepo) {
        this.doctorRepo = doctorRepo;
    }

    public DoctorEntity createDoctor(DoctorTo doctorTo) {
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setName(doctorTo.name());
        doctorEntity.setSurname(doctorTo.surname());
        doctorEntity.setEmail(doctorTo.email());
        doctorEntity.setSpecializationEnums(doctorTo.specializationEnums());
        doctorEntity.setImageUrl(doctorTo.imageUrl());
        return doctorRepo.save(doctorEntity);
    }

    public List<DoctorEntity> getAllDoctors() {
        logger.info(LIST_ALL_DOCTORS);
        return doctorRepo.findAll();
    }
}
