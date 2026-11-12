package com.lunghealth.monitoring.service;

import com.lunghealth.monitoring.dto.request.RiskEvaluationRequest;
import com.lunghealth.monitoring.dto.response.HealthAlertResponse;
import com.lunghealth.monitoring.dto.response.LungFunctionAnalysisResponse;
import com.lunghealth.monitoring.dto.response.RiskAssessmentResponse;
import com.lunghealth.monitoring.exception.ResourceNotFoundException;
import com.lunghealth.monitoring.model.entity.*;
import com.lunghealth.monitoring.repository.HealthAlertRepository;
import com.lunghealth.monitoring.repository.RiskAssessmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final HealthAlertRepository healthAlertRepository;
    private final PatientService patientService;
    private final RespiratoryDataService respiratoryDataService;
    private final SymptomService symptomService;
    private final LungFunctionCalculator lungFunctionCalculator;
    private final RiskAssessmentEngine riskAssessmentEngine;

    public RiskAssessmentService(RiskAssessmentRepository riskAssessmentRepository,
                                 HealthAlertRepository healthAlertRepository,
                                 PatientService patientService,
                                 RespiratoryDataService respiratoryDataService,
                                 SymptomService symptomService,
                                 LungFunctionCalculator lungFunctionCalculator,
                                 RiskAssessmentEngine riskAssessmentEngine) {
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.healthAlertRepository = healthAlertRepository;
        this.patientService = patientService;
        this.respiratoryDataService = respiratoryDataService;
        this.symptomService = symptomService;
        this.lungFunctionCalculator = lungFunctionCalculator;
        this.riskAssessmentEngine = riskAssessmentEngine;
    }

    public RiskAssessmentResponse evaluateRisk(RiskEvaluationRequest request) {
        return evaluateRiskForPatient(request.getPatientId(), request.getRespiratoryDataId(), request.getSymptomLogId());
    }

    public RiskAssessmentResponse evaluateRiskForPatient(Long patientId, Long respDataId, Long symptomLogId) {
        Patient patient = patientService.findEntityById(patientId);

        RespiratoryData respData = respDataId != null
                ? respiratoryDataService.findEntityById(respDataId)
                : respiratoryDataService.getLatestEntity(patientId);

        SymptomLog symptomLog = symptomLogId != null
                ? symptomService.findEntityById(symptomLogId)
                : symptomService.getLatestEntity(patientId);

        LungFunctionAnalysisResponse lungAnalysis = null;
        if (respData != null) {
            lungAnalysis = lungFunctionCalculator.analyze(patient, respData, respData);
        }

        RiskAssessment assessment = riskAssessmentEngine.evaluateRisk(patient, respData, symptomLog, lungAnalysis);
        RiskAssessment saved = riskAssessmentRepository.save(assessment);
        return RiskAssessmentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public RiskAssessmentResponse getLatestAssessment(Long patientId) {
        patientService.findEntityById(patientId);
        return riskAssessmentRepository.findFirstByPatientIdOrderByAssessedAtDesc(patientId)
                .map(RiskAssessmentResponse::fromEntity)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<RiskAssessmentResponse> getAssessmentHistory(Long patientId) {
        patientService.findEntityById(patientId);
        return riskAssessmentRepository.findByPatientIdOrderByAssessedAtDesc(patientId)
                .stream()
                .map(RiskAssessmentResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HealthAlertResponse> getActiveAlerts() {
        return healthAlertRepository.findByAcknowledgedFalseOrderByTimestampDesc()
                .stream()
                .map(HealthAlertResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<HealthAlertResponse> getPatientAlerts(Long patientId) {
        return healthAlertRepository.findByPatientIdOrderByTimestampDesc(patientId)
                .stream()
                .map(HealthAlertResponse::fromEntity)
                .toList();
    }

    public HealthAlertResponse acknowledgeAlert(Long alertId) {
        HealthAlert alert = healthAlertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found with ID: " + alertId));
        alert.setAcknowledged(true);
        alert.setAcknowledgedAt(LocalDateTime.now());
        HealthAlert saved = healthAlertRepository.save(alert);
        return HealthAlertResponse.fromEntity(saved);
    }
}
