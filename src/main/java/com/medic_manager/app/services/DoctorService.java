package com.medic_manager.app.services;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.repositories.DoctorRepo;
import com.medic_manager.app.tos.DoctorTo;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        if (isToInvalid(doctorTo)) {
            logger.info(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
        }
        List<DoctorEntity> doctorsByEmails = doctorRepo.findByEmailIgnoreCase(doctorTo.email());
        if (!doctorsByEmails.isEmpty()) {
            logger.info(getErrorEntityWithPropertyAlreadyExist(DoctorEntity.class, doctorTo.email()));
            throw new EntityExistsException(getErrorEntityWithPropertyAlreadyExist(DoctorEntity.class, doctorTo.email()));
        } else {
            logger.info(getCreateNewEntity(DoctorEntity.class));
            DoctorEntity doctorEntity = new DoctorEntity();
            doctorEntity.setName(doctorTo.name());
            doctorEntity.setSurname(doctorTo.surname());
            doctorEntity.setEmail(doctorTo.email());
            doctorEntity.setSpecializationEnums(doctorTo.specializationEnums());
            doctorEntity.setImageUrl(doctorTo.imageUrl());
            return doctorRepo.save(doctorEntity);
        }
    }

    public List<DoctorEntity> getAllDoctors() {
        logger.info(getListAllEntities(DoctorEntity.class));
        return doctorRepo.findAll();
    }

    public DoctorEntity getDoctorById(Long id) {
        logger.info(getGetEntityById(DoctorEntity.class, id));
        if (id == null) {
            logger.info(getErrorNullPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullPassedAsArgumentToMethod());
        } else {
            return doctorRepo.findById(id)
                    .orElseThrow(
                            () -> {
                                logger.info(getErrorEntityWithIdNotFound(DoctorEntity.class, id));
                                throw new EntityNotFoundException(getErrorEntityWithIdNotFound(DoctorEntity.class, id));
                            }
                    );
        }
    }

    private boolean isToInvalid(DoctorTo doctorTo) {
        logger.info(getCheckingIfToInvalid());
        if (
                doctorTo == null
                        || doctorTo.id() != null
                        || doctorTo.name() == null
                        || doctorTo.surname() == null
                        || doctorTo.email() == null
        ) {
            return true;
        } else {
            return doctorTo.name().isEmpty()
                    || doctorTo.surname().isEmpty()
                    || doctorTo.email().isEmpty()
                    || doctorTo.specializationEnums().isEmpty();
        }
    }
}
