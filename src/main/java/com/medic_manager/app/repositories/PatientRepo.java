package com.medic_manager.app.repositories;

import com.medic_manager.app.entities.PatientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepo extends JpaRepository<PatientEntity, Long> {
    Optional<PatientEntity> findByEmailIgnoreCase(String email);
}
