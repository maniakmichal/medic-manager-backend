package com.medic_manager.app.repositories;

import com.medic_manager.app.entities.PatientEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PatientRepo extends JpaRepository<PatientEntity, Long> {
    Optional<PatientEntity> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"appointmentEntityList"})
    @Query("SELECT p FROM PatientEntity p WHERE p.id = :id")
    Optional<PatientEntity> findByIdWithAppointments(@Param("id") Long id);
}
