package com.medic_manager.app.repositories;

import com.medic_manager.app.entities.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepo extends JpaRepository<DoctorEntity, Long> {
    Optional<DoctorEntity> findByEmailIgnoreCase(String email);
}
