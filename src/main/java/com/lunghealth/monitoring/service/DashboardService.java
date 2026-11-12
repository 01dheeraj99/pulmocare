package com.lunghealth.monitoring.service;

import com.lunghealth.monitoring.dto.response.DashboardMetricsResponse;
import com.lunghealth.monitoring.model.entity.RiskAssessment;
import com.lunghealth.monitoring.model.enums.RiskLevel;
import com.lunghealth.monitoring.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final PatientRepository patientRepository;
    private final RespiratoryDataRepository respiratoryDataRepository;
    private final SymptomLogRepository symptomLogRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final HealthAlertRepository healthAlertRepository;

    public DashboardService(PatientRepository patientRepository,
                            RespiratoryDataRepository respiratoryDataRepository,
                            SymptomLogRepository symptomLogRepository,
                            RiskAssessmentRepository riskAssessmentRepository,
                            HealthAlertRepository healthAlertRepository) {
        this.patientRepository = patientRepository;
        this.respiratoryDataRepository = respiratoryDataRepository;
        this.symptomLogRepository = symptomLogRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.healthAlertRepository = healthAlertRepository;
    }

    public DashboardMetricsResponse getMetrics() {
        DashboardMetricsResponse m = new DashboardMetricsResponse();
        m.setTotalPatients(patientRepository.count());
        m.setTotalRespiratoryReadings(respiratoryDataRepository.count());
        m.setTotalSymptomLogs(symptomLogRepository.count());
        m.setTotalActiveAlerts(healthAlertRepository.countByAcknowledgedFalse());
        m.setCriticalRiskPatients(riskAssessmentRepository.countByRiskLevel(RiskLevel.CRITICAL));
        m.setHighRiskPatients(riskAssessmentRepository.countByRiskLevel(RiskLevel.HIGH));

        Double avgSpo2 = respiratoryDataRepository.getAverageSpo2SystemWide();
        m.setAverageSystemSpo2(avgSpo2 != null ? Math.round(avgSpo2 * 10.0) / 10.0 : 0.0);

        Double avgFev1 = respiratoryDataRepository.getAverageFev1SystemWide();
        m.setAverageSystemFev1(avgFev1 != null ? Math.round(avgFev1 * 100.0) / 100.0 : 0.0);

        Map<String, Long> riskMap = new HashMap<>();
        for (RiskLevel rl : RiskLevel.values()) {
            riskMap.put(rl.name(), riskAssessmentRepository.countByRiskLevel(rl));
        }
        m.setRiskLevelDistribution(riskMap);

        Map<String, Long> patternMap = new HashMap<>();
        List<RiskAssessment> allAssessments = riskAssessmentRepository.findAll();
        for (RiskAssessment ra : allAssessments) {
            if (ra.getObstructionPattern() != null) {
                patternMap.merge(ra.getObstructionPattern().name(), 1L, Long::sum);
            }
        }
        m.setObstructionPatternDistribution(patternMap);

        return m;
    }
}
