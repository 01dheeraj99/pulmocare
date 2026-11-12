package com.lunghealth.monitoring.repository;

import com.lunghealth.monitoring.model.entity.RespiratoryData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RespiratoryDataRepository extends JpaRepository<RespiratoryData, Long> {
    List<RespiratoryData> findByPatientIdOrderByRecordedAtDesc(Long patientId);

    Optional<RespiratoryData> findFirstByPatientIdOrderByRecordedAtDesc(Long patientId);

    @Query("SELECT r FROM RespiratoryData r WHERE r.patient.id = :patientId AND r.recordedAt BETWEEN :start AND :end ORDER BY r.recordedAt ASC")
    List<RespiratoryData> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT AVG(r.spo2) FROM RespiratoryData r")
    Double getAverageSpo2SystemWide();

    @Query("SELECT AVG(r.fev1) FROM RespiratoryData r")
    Double getAverageFev1SystemWide();
}
