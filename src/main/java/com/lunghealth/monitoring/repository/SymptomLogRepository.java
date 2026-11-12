package com.lunghealth.monitoring.repository;

import com.lunghealth.monitoring.model.entity.SymptomLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SymptomLogRepository extends JpaRepository<SymptomLog, Long> {
    List<SymptomLog> findByPatientIdOrderByRecordedAtDesc(Long patientId);

    Optional<SymptomLog> findFirstByPatientIdOrderByRecordedAtDesc(Long patientId);

    @Query("SELECT s FROM SymptomLog s WHERE s.patient.id = :patientId AND s.recordedAt BETWEEN :start AND :end ORDER BY s.recordedAt ASC")
    List<SymptomLog> findByPatientIdAndDateRange(
            @Param("patientId") Long patientId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(s) FROM SymptomLog s WHERE s.patient.id = :patientId AND s.recordedAt >= :since")
    Long countLogsSince(@Param("patientId") Long patientId, @Param("since") LocalDateTime since);
}
