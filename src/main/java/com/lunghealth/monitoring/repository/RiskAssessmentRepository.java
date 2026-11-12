package com.lunghealth.monitoring.repository;

import com.lunghealth.monitoring.model.entity.RiskAssessment;
import com.lunghealth.monitoring.model.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RiskAssessmentRepository extends JpaRepository<RiskAssessment, Long> {
    List<RiskAssessment> findByPatientIdOrderByAssessedAtDesc(Long patientId);

    Optional<RiskAssessment> findFirstByPatientIdOrderByAssessedAtDesc(Long patientId);

    long countByRiskLevel(RiskLevel riskLevel);

    long countByRequiresImmediateAttentionTrue();

    @Query("SELECT r.riskLevel, COUNT(r) FROM RiskAssessment r GROUP BY r.riskLevel")
    List<Object[]> getRiskDistribution();
}
