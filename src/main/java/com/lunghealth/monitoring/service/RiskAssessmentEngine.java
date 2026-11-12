package com.lunghealth.monitoring.service;

import com.lunghealth.monitoring.dto.response.LungFunctionAnalysisResponse;
import com.lunghealth.monitoring.model.entity.*;
import com.lunghealth.monitoring.model.enums.*;
import com.lunghealth.monitoring.repository.HealthAlertRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clinical Decision Support (CDS) Risk Stratification Engine.
 *
 * Synthesizes multidimensional physiological data:
 * 1. Pulse Oximetry (SpO2) and Hypoxemia grading
 * 2. Ventilatory drive (Respiratory Rate) based on NEWS2 clinical thresholds
 * 3. Spirometric obstruction severity (GOLD 1 to 4) and acute decline velocity
 * 4. Patient-reported respiratory symptom complexes (mMRC dyspnea, sputum purulence, hemoptysis)
 * 5. Cumulative environmental and lifestyle exposures (tobacco pack-years, workplace hazards, AQI)
 *
 * Evaluates a normalized composite risk index (0 to 100) and triggers automated alerts
 * when emergency thresholds are crossed.
 */
@Component
public class RiskAssessmentEngine {

    private final HealthAlertRepository alertRepository;

    public RiskAssessmentEngine(HealthAlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public RiskAssessment evaluateRisk(Patient patient,
                                       RespiratoryData respData,
                                       SymptomLog symptomLog,
                                       LungFunctionAnalysisResponse lungAnalysis) {

        double riskScore = 0.0;
        List<String> riskFactors = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        boolean immediateAttentionRequired = false;

        // -------------------------------------------------------------------------
        // 1. Oxygenation Assessment (Pulse Oximetry / SpO2)
        // -------------------------------------------------------------------------
        HypoxemiaSeverity hypoxemia = HypoxemiaSeverity.NORMAL;
        if (respData != null && respData.getSpo2() != null) {
            double spo2 = respData.getSpo2();
            if (spo2 < 85.0) {
                hypoxemia = HypoxemiaSeverity.SEVERE_HYPOXEMIA;
                riskScore += 45.0;
                riskFactors.add("Critical Hypoxemia (SpO2 " + spo2 + "% < 85%)");
                immediateAttentionRequired = true;
                triggerAlert(patient, RiskLevel.CRITICAL, AlertType.SEVERE_HYPOXEMIA,
                        String.format("CRITICAL: Patient %s SpO2 is critically low at %.1f%%. Immediate supplemental oxygen & emergency assessment required.", patient.getFullName(), spo2));
            } else if (spo2 < 90.0) {
                hypoxemia = HypoxemiaSeverity.MODERATE_HYPOXEMIA;
                riskScore += 28.0;
                riskFactors.add("Moderate Hypoxemia (SpO2 " + spo2 + "% < 90%)");
                recommendations.add("Initiate controlled oxygen titration to maintain SpO2 between 92-96% (or 88-92% in chronic hypercapnic COPD).");
            } else if (spo2 < 95.0) {
                hypoxemia = HypoxemiaSeverity.MILD_HYPOXEMIA;
                riskScore += 12.0;
                riskFactors.add("Mild Hypoxemia (SpO2 " + spo2 + "%)");
            }
        }

        // -------------------------------------------------------------------------
        // 2. Respiratory Rate & Vital Signs (NEWS2 Framework)
        // -------------------------------------------------------------------------
        if (respData != null && respData.getRespiratoryRate() != null) {
            int rr = respData.getRespiratoryRate();
            if (rr > 28) {
                riskScore += 22.0;
                riskFactors.add("Severe Tachypnea (Respiratory Rate: " + rr + " bpm)");
                immediateAttentionRequired = true;
                triggerAlert(patient, RiskLevel.CRITICAL, AlertType.TACHYPNEA_WARNING,
                        String.format("URGENT: Patient %s displays severe tachypnea with %d breaths/min.", patient.getFullName(), rr));
            } else if (rr > 22) {
                riskScore += 12.0;
                riskFactors.add("Mild-to-Moderate Tachypnea (" + rr + " bpm)");
                recommendations.add("Monitor work of breathing, accessory muscle use, and repeat vitals every 30 minutes.");
            } else if (rr < 10) {
                riskScore += 20.0;
                riskFactors.add("Bradypnea / Respiratory Depression (" + rr + " bpm)");
                triggerAlert(patient, RiskLevel.HIGH, AlertType.BRADYPNEA_WARNING,
                        String.format("WARNING: Patient %s has significant bradypnea (%d bpm). Check for sedative effects or carbon dioxide narcosis.", patient.getFullName(), rr));
            }
        }

        // -------------------------------------------------------------------------
        // 3. Spirometry & GOLD Severity Staging
        // -------------------------------------------------------------------------
        GoldStage goldStage = GoldStage.NOT_APPLICABLE;
        ObstructionPattern pattern = ObstructionPattern.NORMAL;
        if (lungAnalysis != null) {
            goldStage = lungAnalysis.getGoldStage();
            pattern = lungAnalysis.getObstructionPattern();

            if (pattern == ObstructionPattern.OBSTRUCTIVE || pattern == ObstructionPattern.MIXED) {
                switch (goldStage) {
                    case GOLD_4_VERY_SEVERE -> {
                        riskScore += 35.0;
                        riskFactors.add("Very Severe Airflow Obstruction (GOLD 4, FEV1 < 30%)");
                        recommendations.add("Pulmonologist consultation recommended; consider non-invasive ventilation or long-term oxygen therapy assessment.");
                    }
                    case GOLD_3_SEVERE -> {
                        riskScore += 25.0;
                        riskFactors.add("Severe Airflow Obstruction (GOLD 3, 30% ≤ FEV1 < 50%)");
                        recommendations.add("Optimize dual/triple bronchodilator therapy (LAMA + LABA +/- ICS) and pulmonary rehabilitation.");
                    }
                    case GOLD_2_MODERATE -> {
                        riskScore += 15.0;
                        riskFactors.add("Moderate Airflow Obstruction (GOLD 2, 50% ≤ FEV1 < 80%)");
                        recommendations.add("Ensure adherence to maintenance maintenance inhalers and annual influenza/pneumococcal vaccination.");
                    }
                    case GOLD_1_MILD -> {
                        riskScore += 8.0;
                        riskFactors.add("Mild Airflow Obstruction (GOLD 1)");
                    }
                    default -> {}
                }
            } else if (pattern == ObstructionPattern.RESTRICTIVE) {
                riskScore += 18.0;
                riskFactors.add("Restrictive Spirometric Pattern (Reduced FVC)");
                recommendations.add("Schedule high-resolution chest CT (HRCT) and DLCO testing to evaluate interstitial or thoracic restriction.");
            }

            // Accelerated longitudinal loss alert
            if ("RAPID_DECLINE".equals(lungAnalysis.getTrendDirection())) {
                riskScore += 15.0;
                riskFactors.add("Accelerated FEV1 decline (>12% fall compared to baseline)");
                triggerAlert(patient, RiskLevel.HIGH, AlertType.ACUTE_AIRFLOW_OBSTRUCTION,
                        String.format("ALERT: Patient %s shows rapid FEV1 loss (>12%% drop) indicating potential acute exacerbation.", patient.getFullName()));
            }
        }

        // -------------------------------------------------------------------------
        // 4. Symptom Complex & Anthonisen Exacerbation Criteria
        // -------------------------------------------------------------------------
        AsthmaControlLevel asthmaControl = AsthmaControlLevel.NOT_APPLICABLE;
        if (symptomLog != null) {
            double symScore = symptomLog.getTotalSymptomScore() != null ? symptomLog.getTotalSymptomScore() : 0.0;
            riskScore += (symScore * 0.7);

            // Red-flag symptom: Hemoptysis (bloody sputum)
            if (symptomLog.getCoughType() == CoughType.HEMOPTYSIS ||
                symptomLog.getSputumColor() == SputumColor.BLOOD_STAINED) {
                riskScore += 25.0;
                riskFactors.add("Hemoptysis / Blood-tinged sputum reported");
                immediateAttentionRequired = true;
                triggerAlert(patient, RiskLevel.CRITICAL, AlertType.HEMOPTYSIS_ALERT,
                        String.format("EMERGENCY: Patient %s reported hemoptysis / bloody sputum. Urgent clinical evaluation required.", patient.getFullName()));
                recommendations.add("URGENT: Rule out pulmonary embolism, acute airway trauma, cavitary lesion, or active tuberculosis.");
            }

            // Bacterial infection indicators (Purulent green/yellow sputum + fever)
            if (symptomLog.getSputumColor() == SputumColor.GREEN ||
                symptomLog.getSputumColor() == SputumColor.YELLOW ||
                symptomLog.getSputumColor() == SputumColor.RUST_BROWN) {
                riskScore += 12.0;
                riskFactors.add("Purulent sputum (" + symptomLog.getSputumColor() + ") suggestive of bacterial tracheobronchitis or pneumonia");
                recommendations.add("Consider sputum culture and early targeted antibacterial course if Anthonisen criteria for COPD exacerbation are met.");
                if (Boolean.TRUE.equals(symptomLog.getFeverPresent())) {
                    triggerAlert(patient, RiskLevel.HIGH, AlertType.PURULENT_INFECTION_SUSPECTED,
                            String.format("WARNING: Patient %s has purulent sputum with fever, suggesting acute lower respiratory tract infection.", patient.getFullName()));
                }
            }

            // Functional dyspnea burden (mMRC)
            if (symptomLog.getShortnessOfBreathScale() >= 3) {
                riskScore += 10.0;
                riskFactors.add("Severe Functional Dyspnea (mMRC grade " + symptomLog.getShortnessOfBreathScale() + ")");
                recommendations.add("Instruct patient on energy conservation, pursed-lip breathing, and evaluate rescue bronchodilator frequency.");
            }

            // GINA Asthma control assessment
            String primaryDx = patient.getPrimaryDiagnosis() != null ? patient.getPrimaryDiagnosis().toUpperCase() : "";
            if (primaryDx.contains("ASTHMA")) {
                int asthmaMarkers = 0;
                if (Boolean.TRUE.equals(symptomLog.getNocturnalAwakening())) asthmaMarkers++;
                if (Boolean.TRUE.equals(symptomLog.getActivityLimitation())) asthmaMarkers++;
                if (Boolean.TRUE.equals(symptomLog.getWheezingPresent())) asthmaMarkers++;
                if (symptomLog.getCoughSeverity() == SymptomSeverity.SEVERE || symptomLog.getCoughSeverity() == SymptomSeverity.MODERATE) asthmaMarkers++;

                if (asthmaMarkers >= 3) {
                    asthmaControl = AsthmaControlLevel.UNCONTROLLED;
                    riskScore += 18.0;
                    riskFactors.add("GINA Uncontrolled Asthma features (night awakening, wheeze, daytime limitation)");
                    recommendations.add("Step up anti-inflammatory controller therapy according to GINA track; review inhaler inhalation technique.");
                    triggerAlert(patient, RiskLevel.HIGH, AlertType.ASTHMA_FLARE_UP,
                            String.format("ALERT: Patient %s exhibits uncontrolled asthma symptoms with nocturnal awakening.", patient.getFullName()));
                } else if (asthmaMarkers >= 1) {
                    asthmaControl = AsthmaControlLevel.PARTLY_CONTROLLED;
                    riskScore += 8.0;
                    riskFactors.add("Partly controlled asthma symptoms");
                } else {
                    asthmaControl = AsthmaControlLevel.WELL_CONTROLLED;
                }
            }
        }

        // -------------------------------------------------------------------------
        // 5. Environmental & Modifiable Risk Factors
        // -------------------------------------------------------------------------
        if (respData != null && respData.getAqiIndex() != null && respData.getAqiIndex() > 150) {
            riskScore += 8.0;
            riskFactors.add("High ambient Air Quality Index (" + respData.getAqiIndex() + " - Unhealthy air)");
            recommendations.add("Advise staying indoors with HEPA air filtration and avoiding outdoor cardiovascular exertion.");
        }

        if (patient.getSmokingStatus() == SmokingStatus.CURRENT_SMOKER) {
            double packBonus = Math.min(15.0, (patient.getPackYears() != null ? patient.getPackYears() : 0.0) * 0.4);
            riskScore += (6.0 + packBonus);
            riskFactors.add(String.format("Active tobacco smoking (%.1f pack-years cumulative history)", patient.getPackYears()));
            recommendations.add("Strongly recommend smoking cessation program, nicotine replacement therapy (NRT) or varenicline.");
        } else if (patient.getSmokingStatus() == SmokingStatus.FORMER_SMOKER && patient.getPackYears() != null && patient.getPackYears() > 20) {
            riskScore += 5.0;
            riskFactors.add(String.format("Significant past smoking history (%.1f pack-years)", patient.getPackYears()));
            recommendations.add("Annual low-dose CT lung cancer screening indicated based on >20 pack-year smoking history.");
        }

        if (patient.getOccupationalExposure() != null && !patient.getOccupationalExposure().isBlank() && !"NONE".equalsIgnoreCase(patient.getOccupationalExposure())) {
            riskScore += 5.0;
            riskFactors.add("Occupational exposure hazard: " + patient.getOccupationalExposure());
            recommendations.add("Ensure certified PPE (N95 or particulate respirator) at work; notify workplace occupational health.");
        }

        // Clamp final score between 0 and 100
        double finalScore = Math.min(100.0, Math.round(riskScore * 10.0) / 10.0);

        RiskLevel level;
        if (finalScore >= 80.0 || immediateAttentionRequired) {
            level = RiskLevel.CRITICAL;
        } else if (finalScore >= 55.0) {
            level = RiskLevel.HIGH;
        } else if (finalScore >= 25.0) {
            level = RiskLevel.MODERATE;
        } else {
            level = RiskLevel.LOW;
        }

        if (recommendations.isEmpty()) {
            recommendations.add("Maintain regular pulmonary wellness routine and routine spirometry monitoring.");
            recommendations.add("Stay physically active with moderate aerobic exercises.");
        }

        RiskAssessment assessment = new RiskAssessment();
        assessment.setPatient(patient);
        assessment.setAssessedAt(LocalDateTime.now());
        assessment.setOverallRiskScore(finalScore);
        assessment.setRiskLevel(level);
        assessment.setGoldStage(goldStage);
        assessment.setAsthmaControlLevel(asthmaControl);
        assessment.setHypoxemiaSeverity(hypoxemia);
        assessment.setObstructionPattern(pattern);
        assessment.setRiskFactors(String.join(" || ", riskFactors));
        assessment.setClinicalRecommendations(String.join(" || ", recommendations));
        assessment.setRequiresImmediateAttention(immediateAttentionRequired || level == RiskLevel.CRITICAL);

        if (respData != null) assessment.setRespiratoryDataId(respData.getId());
        if (symptomLog != null) assessment.setSymptomLogId(symptomLog.getId());

        return assessment;
    }

    private void triggerAlert(Patient patient, RiskLevel severity, AlertType type, String message) {
        HealthAlert alert = new HealthAlert(patient, severity, type, message);
        alertRepository.save(alert);
    }
}
