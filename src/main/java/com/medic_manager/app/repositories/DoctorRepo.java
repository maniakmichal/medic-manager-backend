package com.medic_manager.app.repositories;

import com.medic_manager.app.entities.DoctorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepo extends JpaRepository<DoctorEntity, Long> {
    List<DoctorEntity> findByEmailIgnoreCase(String email);
}
