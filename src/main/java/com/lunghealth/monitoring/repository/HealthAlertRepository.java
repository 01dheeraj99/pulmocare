package com.lunghealth.monitoring.repository;

import com.lunghealth.monitoring.model.entity.HealthAlert;
import com.lunghealth.monitoring.model.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthAlertRepository extends JpaRepository<HealthAlert, Long> {
    List<HealthAlert> findByAcknowledgedFalseOrderByTimestampDesc();

    List<HealthAlert> findByPatientIdOrderByTimestampDesc(Long patientId);

    List<HealthAlert> findBySeverityAndAcknowledgedFalse(RiskLevel severity);

    long countByAcknowledgedFalse();
}
