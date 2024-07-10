package com.medic_manager.app.services;

import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.repositories.PatientRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

@Service
@Transactional
public class PatientService {

    private static final String LIST_ALL_PATIENTS = "List all patients.";

    private final PatientRepo patientRepo;
    private final Logger logger = Logger.getLogger(PatientService.class.getName());

    public PatientService(final PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    List<PatientEntity> getAllPatients() {
        logger.info(LIST_ALL_PATIENTS);
        return patientRepo.findAll();
    }
}
