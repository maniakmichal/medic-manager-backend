package com.medic_manager.app.repositories;

import com.medic_manager.app.entities.DutyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface DutyRepo extends JpaRepository<DutyEntity, Long> {

    @Query("""
            SELECT COUNT(d) FROM DutyEntity d
            WHERE d.doctorEntity.id = :doctorId
            AND (
                (:startDate BETWEEN d.startDate AND d.endDate)
                OR (:endDate BETWEEN d.startDate AND d.endDate)
                OR (d.startDate BETWEEN :startDate AND :endDate)
            )
            """)
    int countDutyByDoctorIdAndDatesBetween(
            @Param("doctorId") Long doctorId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
