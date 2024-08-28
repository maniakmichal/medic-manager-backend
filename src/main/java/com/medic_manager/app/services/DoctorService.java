package com.medic_manager.app.services;

import com.medic_manager.app.common.LoggerTextUtil;
import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.enums.SpecializationEnum;
import com.medic_manager.app.repositories.DoctorRepo;
import com.medic_manager.app.tos.DoctorTo;
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
public class DoctorService {

    private final DoctorRepo doctorRepo;
    private final Logger logger = Logger.getLogger(DoctorService.class.getName());

    public DoctorService(final DoctorRepo doctorRepo) {
        this.doctorRepo = doctorRepo;
    }

    public DoctorEntity createDoctor(DoctorTo doctorTo) {
        validateCreateTo(doctorTo);
        checkIfEntityAlreadyExist(doctorTo.email());
        logger.info(() -> getCreateNewEntity(DoctorEntity.class, doctorTo));
        DoctorEntity doctorEntity = generateDoctor(doctorTo);
        return doctorRepo.save(doctorEntity);
    }

    public List<DoctorEntity> getAllDoctors() {
        logger.info(() -> getListAllEntities(DoctorEntity.class));
        return doctorRepo.findAll();
    }

    public DoctorEntity getDoctorById(Long id) {
        logger.info(() -> getGetEntityById(DoctorEntity.class, id));
        if (id == null) {
            logger.severe(LoggerTextUtil::getErrorNullPassedAsArgumentToMethod);
            throw new IllegalArgumentException(getErrorNullPassedAsArgumentToMethod());
        }
        return doctorRepo.findById(id)
                .orElseThrow(
                        () -> {
                            logger.severe(getErrorEntityWithIdNotFound(DoctorEntity.class, id));
                            return new EntityNotFoundException(getErrorEntityWithIdNotFound(DoctorEntity.class, id));
                        }
                );
    }

    public DoctorEntity updateDoctor(DoctorTo doctorTo) {
        validateUpdateTo(doctorTo);
        checkIfEntityAlreadyExist(doctorTo.email());
        DoctorEntity persistedDoctor = doctorRepo.findById(doctorTo.id())
                .orElseThrow(() -> {
                    logger.severe(getErrorEntityWithIdNotFound(DoctorEntity.class, doctorTo.id()));
                    return new EntityNotFoundException(getErrorEntityWithIdNotFound(DoctorEntity.class, doctorTo.id()));
                });
        logger.info(() -> getUpdateEntity(DoctorEntity.class, doctorTo));
        DoctorEntity doctorEntity = updateDoctorEntity(persistedDoctor, doctorTo);
        return doctorRepo.save(doctorEntity);
    }

    public void deleteDoctor(Long id) {
        if (id == null) {
            logger.severe(getErrorNullPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullPassedAsArgumentToMethod());
        }
        logger.info(() -> getDeleteEntityById(DoctorEntity.class, id));
        doctorRepo.deleteById(id);
    }

    private DoctorEntity generateDoctor(DoctorTo doctorTo) {
        DoctorEntity doctorEntity = new DoctorEntity();
        doctorEntity.setName(doctorTo.name());
        doctorEntity.setSurname(doctorTo.surname());
        doctorEntity.setEmail(doctorTo.email());
        doctorEntity.setSpecializationEnums(doctorTo.specializationEnums());
        doctorEntity.setImageUrl(doctorTo.imageUrl());
        return doctorEntity;
    }

    private DoctorEntity updateDoctorEntity(DoctorEntity persistedDoctor, DoctorTo doctorTo) {
        persistedDoctor.setName(doctorTo.name());
        persistedDoctor.setSurname(doctorTo.surname());
        persistedDoctor.setEmail(doctorTo.email());
        persistedDoctor.setSpecializationEnums(doctorTo.specializationEnums());
        persistedDoctor.setImageUrl(doctorTo.imageUrl());
        return persistedDoctor;
    }

    private void checkIfEntityAlreadyExist(String email) throws EntityExistsException {
        Optional<DoctorEntity> doctorByEmail = doctorRepo.findByEmailIgnoreCase(email);
        if (doctorByEmail.isPresent()) {
            logger.severe(() -> getErrorEntityWithPropertyAlreadyExist(DoctorEntity.class, email));
            throw new EntityExistsException(getErrorEntityWithPropertyAlreadyExist(DoctorEntity.class, email));
        }
    }

    private void validateCreateTo(DoctorTo doctorTo) throws IllegalArgumentException {
        if (isToInvalid(doctorTo) || doctorTo.id() != null) {
            logger.severe(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
        }
    }

    private void validateUpdateTo(DoctorTo doctorTo) throws IllegalArgumentException {
        if (isToInvalid(doctorTo) || doctorTo.id() == null) {
            logger.severe(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
        }
    }

    private boolean isToInvalid(DoctorTo doctorTo) {
        logger.info(LoggerTextUtil::getCheckingIfToInvalid);
        if (
                doctorTo == null
                        || doctorTo.name() == null
                        || doctorTo.surname() == null
                        || doctorTo.email() == null
                        || isListOfEnumsNull(doctorTo.specializationEnums())
        ) {
            return true;
        } else {
            return doctorTo.name().isEmpty()
                    || doctorTo.surname().isEmpty()
                    || doctorTo.email().isEmpty()
                    || doctorTo.specializationEnums().isEmpty();
        }
    }

    private boolean isListOfEnumsNull(List<SpecializationEnum> list) {
        return list == null;
    }
}
