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

    public static final String CHECKING_IF_TO_INVALID = "Checking if TO invalid.";
    public static final String ERROR_NULL_OR_INCORRECT_TO_PASSED_AS_ARGUMENT_TO_METHOD = "ERROR: null or incorrect TO passed as argument to method";
    private static final String LIST_ALL_DOCTORS = "List all doctors.";

    private final DoctorRepo doctorRepo;
    private final Logger logger = Logger.getLogger(DoctorService.class.getName());

    public DoctorService(final DoctorRepo doctorRepo) {
        this.doctorRepo = doctorRepo;
    }

    public DoctorEntity createDoctor(DoctorTo doctorTo) {
        if (isToInvalid(doctorTo)) {
            logger.info(ERROR_NULL_OR_INCORRECT_TO_PASSED_AS_ARGUMENT_TO_METHOD);
            throw new IllegalArgumentException(ERROR_NULL_OR_INCORRECT_TO_PASSED_AS_ARGUMENT_TO_METHOD);
        }
        List<DoctorEntity> doctorsByEmails = doctorRepo.findByEmailIgnoreCase(doctorTo.email());
        if (!doctorsByEmails.isEmpty()) {
            throw new IllegalArgumentException("HERE SHOULD BE SPECIFIC EXCEPTION.");
        } else {
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
        logger.info(LIST_ALL_DOCTORS);
        return doctorRepo.findAll();
    }

    private boolean isToInvalid(DoctorTo doctorTo) {
        logger.info(CHECKING_IF_TO_INVALID);
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
