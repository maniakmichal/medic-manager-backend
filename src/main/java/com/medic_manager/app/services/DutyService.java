package com.medic_manager.app.services;

import com.medic_manager.app.entities.DoctorEntity;
import com.medic_manager.app.entities.DutyEntity;
import com.medic_manager.app.exceptions.DutyCreationFailedBusinessException;
import com.medic_manager.app.exceptions.InvalidDatesProvidedBusinessException;
import com.medic_manager.app.repositories.DutyRepo;
import com.medic_manager.app.tos.DutyTo;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.logging.Logger;

import static com.medic_manager.app.common.LoggerTextUtil.*;

@Service
@Transactional
public class DutyService {

    private final DutyRepo dutyRepo;
    private final DoctorService doctorService;
    private final Logger logger = Logger.getLogger(DutyService.class.getName());

    public DutyService(DutyRepo dutyRepo, DoctorService doctorService) {
        this.dutyRepo = dutyRepo;
        this.doctorService = doctorService;
    }

    public DutyEntity createDuty(DutyTo dutyTo) {
        validateCreateTo(dutyTo);
        isValidStartAndEndDate(dutyTo);
        DoctorEntity doctorEntity = doctorService.getDoctorById(dutyTo.doctorId());
        checkIfDoctorHasDutyAlready(dutyTo);
        logger.info(() -> getCreateNewEntity(DutyEntity.class, dutyTo));
        DutyEntity dutyEntity = generateDuty(dutyTo, doctorEntity);
        return dutyRepo.save(dutyEntity);
    }

    public List<DutyEntity> getAllDuties() {
        logger.info(() -> getListAllEntities(DutyEntity.class));
        return dutyRepo.findAll();
    }

    public DutyEntity getDutyById(Long id) {
        logger.info(() -> getGetEntityById(DutyEntity.class, id));
        return findById(id);
    }

    public DutyEntity updateDuty(DutyTo dutyTo) {
        validateUpdateTo(dutyTo);
        isValidStartAndEndDate(dutyTo);
        logger.info(() -> getUpdateEntity(DutyEntity.class, dutyTo));
        DutyEntity persistedDuty = findById(dutyTo.id());
        isSetDoctorToUpdate(dutyTo, persistedDuty);
        persistedDuty.setStartDate(dutyTo.startDate());
        persistedDuty.setEndDate(dutyTo.endDate());
        return dutyRepo.save(persistedDuty);
    }

    public void deleteDuty(Long id) {
        if (id == null) {
            logger.severe(getErrorNullPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullPassedAsArgumentToMethod());
        }
        dutyRepo.deleteById(id);
    }

    private DutyEntity findById(Long id) {
        if (id == null) {
            logger.severe(getErrorNullPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullPassedAsArgumentToMethod());
        }
        return dutyRepo.findById(id)
                .orElseThrow(
                        () -> {
                            logger.severe(getErrorEntityWithIdNotFound(DutyEntity.class, id));
                            return new EntityNotFoundException(getErrorEntityWithIdNotFound(DutyEntity.class, id));
                        }
                );
    }

    private void validateCreateTo(DutyTo dutyTo) {
        if (isToInvalid(dutyTo) || dutyTo.id() != null) {
            logger.severe(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
        }
    }

    private void validateUpdateTo(DutyTo dutyTo) {
        if (isToInvalid(dutyTo) || dutyTo.id() == null) {
            logger.severe(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
            throw new IllegalArgumentException(getErrorNullOrIncorrectTOPassedAsArgumentToMethod());
        }
    }

    private boolean isToInvalid(DutyTo dutyTo) {
        logger.info(getCheckingIfToInvalid());
        return dutyTo == null
                || dutyTo.doctorId() == null
                || dutyTo.startDate() == null
                || dutyTo.endDate() == null;
    }

    private void isSetDoctorToUpdate(
            DutyTo dutyTo,
            DutyEntity persistedDuty
    ) {
        boolean isDoctorChanged = !persistedDuty.getDoctorEntity().getId().equals(dutyTo.doctorId());
        if (isDoctorChanged) {
            DoctorEntity persistedDoctor = doctorService.getDoctorById(dutyTo.doctorId());
            checkIfDoctorHasDutyAlready(dutyTo);
            persistedDuty.setDoctorEntity(persistedDoctor);
        }
    }

    private void checkIfDoctorHasDutyAlready(DutyTo dutyTo) {
        int numberOfDuties =
                dutyRepo.countDutyByDoctorIdAndDatesBetween(dutyTo.doctorId(), dutyTo.startDate(), dutyTo.endDate());
        if (numberOfDuties > 0) {
            String message = getErrorDoctorHasDuty().formatted(dutyTo.doctorId());
            logger.severe(() -> getErrorDutyCreationFailedDueTo() + message);
            throw new DutyCreationFailedBusinessException(getErrorDutyCreationFailedDueTo() + message);
        }
    }

    private void isValidStartAndEndDate(DutyTo dutyTo) {
        boolean invalidDate = dutyTo.startDate().isAfter(dutyTo.endDate());
        if (invalidDate) {
            logger.severe(() -> getErrorInvalidDatesProvided().formatted(dutyTo.startDate(), dutyTo.endDate()));
            throw new InvalidDatesProvidedBusinessException(
                    getErrorInvalidDatesProvided().formatted(dutyTo.startDate(), dutyTo.endDate()));
        }
    }

    private DutyEntity generateDuty(
            DutyTo dutyTo,
            DoctorEntity doctorEntity
    ) {
        DutyEntity dutyEntity = new DutyEntity();
        dutyEntity.setDoctorEntity(doctorEntity);
        dutyEntity.setStartDate(dutyTo.startDate());
        dutyEntity.setEndDate(dutyTo.endDate());
        return dutyEntity;
    }
}
