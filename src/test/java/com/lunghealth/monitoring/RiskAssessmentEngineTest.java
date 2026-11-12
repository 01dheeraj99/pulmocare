package com.lunghealth.monitoring;

import com.lunghealth.monitoring.dto.response.LungFunctionAnalysisResponse;
import com.lunghealth.monitoring.model.entity.*;
import com.lunghealth.monitoring.model.enums.*;
import com.lunghealth.monitoring.repository.HealthAlertRepository;
import com.lunghealth.monitoring.service.RiskAssessmentEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RiskAssessmentEngineTest {

    private HealthAlertRepository alertRepository;
    private RiskAssessmentEngine engine;
    private Patient patient;

    @BeforeEach
    void setUp() {
        alertRepository = Mockito.mock(HealthAlertRepository.class);
        engine = new RiskAssessmentEngine(alertRepository);

        patient = new Patient();
        patient.setId(100L);
        patient.setFullName("Arthur Dent");
        patient.setSex(BiologicalSex.MALE);
        patient.setDateOfBirth(LocalDate.now().minusYears(60));
        patient.setHeightCm(178.0);
        patient.setWeightKg(80.0);
        patient.setSmokingStatus(SmokingStatus.CURRENT_SMOKER);
        patient.setPackYears(30.0);
        patient.setPrimaryDiagnosis("COPD");
    }

    @Test
    @DisplayName("Critical Hypoxemia (SpO2 < 85%) should trigger CRITICAL risk and save HealthAlert")
    void testCriticalHypoxemiaTrigger() {
        RespiratoryData resp = new RespiratoryData();
        resp.setId(1L);
        resp.setSpo2(82.0);
        resp.setRespiratoryRate(22);
        resp.setFev1(1.5);
        resp.setFvc(2.8);

        RiskAssessment assessment = engine.evaluateRisk(patient, resp, null, null);

        assertEquals(RiskLevel.CRITICAL, assessment.getRiskLevel());
        assertTrue(assessment.getRequiresImmediateAttention());
        assertEquals(HypoxemiaSeverity.SEVERE_HYPOXEMIA, assessment.getHypoxemiaSeverity());

        ArgumentCaptor<HealthAlert> alertCaptor = ArgumentCaptor.forClass(HealthAlert.class);
        verify(alertRepository, atLeastOnce()).save(alertCaptor.capture());
        HealthAlert savedAlert = alertCaptor.getValue();
        assertEquals(AlertType.SEVERE_HYPOXEMIA, savedAlert.getAlertType());
        assertEquals(RiskLevel.CRITICAL, savedAlert.getSeverity());
    }

    @Test
    @DisplayName("Hemoptysis symptom should trigger emergency alert and clinical recommendation")
    void testHemoptysisTrigger() {
        RespiratoryData resp = new RespiratoryData();
        resp.setSpo2(95.0);
        resp.setRespiratoryRate(18);

        SymptomLog sym = new SymptomLog();
        sym.setId(5L);
        sym.setCoughSeverity(SymptomSeverity.SEVERE);
        sym.setCoughType(CoughType.HEMOPTYSIS);
        sym.setSputumColor(SputumColor.BLOOD_STAINED);
        sym.setShortnessOfBreathScale(3);
        sym.calculateScore();

        RiskAssessment assessment = engine.evaluateRisk(patient, resp, sym, null);

        assertTrue(assessment.getRequiresImmediateAttention());
        assertTrue(assessment.getRiskFactors().contains("Hemoptysis"));
        assertTrue(assessment.getClinicalRecommendations().contains("URGENT"));

        verify(alertRepository, atLeastOnce()).save(any(HealthAlert.class));
    }

    @Test
    @DisplayName("Healthy patient with optimal telemetry should evaluate to LOW risk")
    void testLowRiskEvaluation() {
        Patient healthyPatient = new Patient();
        healthyPatient.setId(101L);
        healthyPatient.setFullName("Healthy Runner");
        healthyPatient.setSex(BiologicalSex.FEMALE);
        healthyPatient.setDateOfBirth(LocalDate.now().minusYears(28));
        healthyPatient.setSmokingStatus(SmokingStatus.NEVER_SMOKER);
        healthyPatient.setPackYears(0.0);

        RespiratoryData resp = new RespiratoryData();
        resp.setSpo2(99.0);
        resp.setRespiratoryRate(14);

        LungFunctionAnalysisResponse lf = new LungFunctionAnalysisResponse();
        lf.setObstructionPattern(ObstructionPattern.NORMAL);
        lf.setGoldStage(GoldStage.NOT_APPLICABLE);
        lf.setTrendDirection("STABLE");

        SymptomLog sym = new SymptomLog();
        sym.setCoughSeverity(SymptomSeverity.NONE);
        sym.setShortnessOfBreathScale(0);
        sym.calculateScore();

        RiskAssessment assessment = engine.evaluateRisk(healthyPatient, resp, sym, lf);

        assertEquals(RiskLevel.LOW, assessment.getRiskLevel());
        assertFalse(assessment.getRequiresImmediateAttention());
        assertTrue(assessment.getOverallRiskScore() < 25.0);
        verify(alertRepository, never()).save(any(HealthAlert.class));
    }
}
