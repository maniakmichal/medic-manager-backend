package com.medic_manager.app.services;

import com.medic_manager.app.entities.PatientEntity;
import com.medic_manager.app.repositories.PatientRepo;
import com.medic_manager.app.tos.PatientTo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import static com.medic_manager.app.common.LoggerTextUtil.*;

@Service
@Transactional
public class PatientService {

    private final PatientRepo patientRepo;
    private final Logger logger = Logger.getLogger(PatientService.class.getName());

    public PatientService(final PatientRepo patientRepo) {
        this.patientRepo = patientRepo;
    }

    public PatientEntity createPatient(PatientTo patientTo) {
        validateCreateTo(patientTo);
        checkIfEntityAlreadyExist(patientTo.email());
        logger.info(() -> getCreateNewEntity(PatientEntity.class, patientTo));
        PatientEntity patientEntity = generatePatient(patientTo);
        return patientRepo.save(patientEntity);
    }

    public List<PatientEntity> getAllPatients() {
        logger.info(() -> getListAllEntities(PatientEntity.class));
        return patientRepo.findAll();
    }

    public PatientEntity getPatientById(Long id) {
        logger.info(() -> getGetEntityById(PatientEntity.class, id));
        return findById(id);
    }

    public PatientEntity updatePatient(PatientTo patientTo) {
        validateUpdateTo(patientTo);
        checkIfEntityAlreadyExist(patientTo.email());
        PatientEntity persistedPatient = findById(patientTo.id());
        logger.info(() -> getUpdateEntity(PatientEntity.class, patientTo));
        PatientEntity patientEntity = updatePatientEntity(persistedPatient, patientTo);
        return patientRepo.save(patientEntity);
    }

    public void deletePatient(Long id) {
        if (id == null) {
            logger.severe(getErrorNullPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullPassedAsArgumentToMethod());
        }
        logger.info(() -> getDeleteEntityById(PatientEntity.class, id));
        patientRepo.deleteById(id);
    }

    private PatientEntity findById(Long id) {
        if (id == null) {
            logger.severe(getErrorNullPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullPassedAsArgumentToMethod());
        }
        return patientRepo.findById(id)
                .orElseThrow(
                        () -> {
                            logger.severe(getErrorEntityWithIdNotFound(PatientEntity.class, id));
                            return new EntityNotFoundException(getErrorEntityWithIdNotFound(PatientEntity.class, id));
                        }
                );
    }

    private PatientEntity generatePatient(PatientTo patientTo) {
        PatientEntity patientEntity = new PatientEntity();
        patientEntity.setName(patientTo.name());
        patientEntity.setSurname(patientTo.surname());
        patientEntity.setEmail(patientTo.email());
        patientEntity.setBirthdate(patientTo.birthdate());
        patientEntity.setGenderEnum(patientTo.genderEnum());
        return patientEntity;
    }

    private PatientEntity updatePatientEntity(PatientEntity persistedPatient, PatientTo patientTo) {
        persistedPatient.setName(patientTo.name());
        persistedPatient.setSurname(patientTo.surname());
        persistedPatient.setEmail(patientTo.email());
        persistedPatient.setBirthdate(patientTo.birthdate());
        persistedPatient.setGenderEnum(patientTo.genderEnum());
        return persistedPatient;
    }

    private void checkIfEntityAlreadyExist(String email) throws EntityExistsException {
        Optional<PatientEntity> patientByEmail = patientRepo.findByEmailIgnoreCase(email);
        if (patientByEmail.isPresent()) {
            logger.severe(() -> getErrorEntityWithPropertyAlreadyExist(PatientEntity.class, email));
            throw new EntityExistsException(getErrorEntityWithPropertyAlreadyExist(PatientEntity.class, email));
        }
    }

    private void validateCreateTo(PatientTo patientTo) throws IllegalArgumentException {
        if (isToInvalid(patientTo) || patientTo.id() != null) {
            logger.severe(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
        }
    }

    private void validateUpdateTo(PatientTo patientTo) throws IllegalArgumentException {
        if (isToInvalid(patientTo) || patientTo.id() == null) {
            logger.severe(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
        }
    }

    private boolean isToInvalid(PatientTo patientTo) {
        logger.info(getCheckingIfToInvalid());
        return patientTo == null
                || patientTo.name() == null
                || patientTo.surname() == null
                || patientTo.email() == null
                || patientTo.genderEnum() == null
                || patientTo.birthdate() == null
                || patientTo.name().isBlank()
                || patientTo.surname().isBlank()
                || patientTo.email().isBlank();
    }
}
