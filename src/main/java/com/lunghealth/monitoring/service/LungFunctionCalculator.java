package com.lunghealth.monitoring.service;

import com.lunghealth.monitoring.dto.response.LungFunctionAnalysisResponse;
import com.lunghealth.monitoring.model.entity.Patient;
import com.lunghealth.monitoring.model.entity.RespiratoryData;
import com.lunghealth.monitoring.model.enums.BiologicalSex;
import com.lunghealth.monitoring.model.enums.GoldStage;
import com.lunghealth.monitoring.model.enums.ObstructionPattern;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Computes predicted spirometric normals and classifies ventilatory mechanics.
 *
 * Implements reference equations based on standard ATS/ERS (American Thoracic
 * Society / European Respiratory Society) recommendations using Knudson & NHANES III
 * regression coefficients for adult populations.
 *
 * Diagnostic criteria:
 * - Airflow obstruction: post-bronchodilator FEV1/FVC &lt; 0.70 (GOLD criteria)
 * - Restrictive pattern: FEV1/FVC &gt;= 0.70 with FVC &lt; 80% predicted (requires TLC to confirm)
 * - Mixed defect: FEV1/FVC &lt; 0.70 and FVC &lt; 80% predicted
 */
@Component
public class LungFunctionCalculator {

    public record PredictedValues(double fev1, double fvc, double fev1FvcRatio, double pef) {}

    /**
     * Calculates expected spirometric reference values adjusted for age, height, and biological sex.
     * Clamps values to physiologically realistic minimums to prevent negative predicted volumes
     * in extreme edge cases (e.g., frail elderly patients).
     */
    public PredictedValues calculatePredictedValues(Patient patient) {
        int age = Math.max(18, patient.getAge());
        double height = patient.getHeightCm() != null ? patient.getHeightCm() : 170.0;
        BiologicalSex sex = patient.getSex() != null ? patient.getSex() : BiologicalSex.MALE;

        double predFvc;
        double predFev1;
        double predPef;

        if (sex == BiologicalSex.MALE) {
            // Standard adult male linear regression (Knudson / NHANES III)
            predFvc = (0.0576 * height) - (0.0269 * age) - 4.34;
            predFev1 = (0.0430 * height) - (0.0290 * age) - 2.49;
            predPef = ((0.070 * height) - (0.030 * age) + 1.5) * 60.0;
        } else {
            // Standard adult female linear regression
            predFvc = (0.0443 * height) - (0.0260 * age) - 2.89;
            predFev1 = (0.0395 * height) - (0.0250 * age) - 2.60;
            predPef = ((0.055 * height) - (0.025 * age) + 1.2) * 60.0;
        }

        // Clamp minimum bounds to avoid non-physiological zero or negative predicted numbers
        predFvc = Math.max(1.5, round(predFvc, 2));
        predFev1 = Math.max(1.2, round(predFev1, 2));
        predPef = Math.max(200.0, round(predPef, 1));
        double predRatio = round(predFev1 / predFvc, 3);

        return new PredictedValues(predFev1, predFvc, predRatio, predPef);
    }

    /**
     * Evaluates a measured spirometry session against predicted norms and historical baseline.
     */
    public LungFunctionAnalysisResponse analyze(Patient patient, RespiratoryData reading, RespiratoryData baseline) {
        PredictedValues pred = calculatePredictedValues(patient);

        double fev1Meas = reading.getFev1();
        double fvcMeas = reading.getFvc();
        double ratioMeas = reading.getFev1FvcRatio() != null
                ? reading.getFev1FvcRatio()
                : round(fev1Meas / fvcMeas, 3);
        double pefMeas = reading.getPef() != null ? reading.getPef() : 0.0;

        double fev1Pct = round((fev1Meas / pred.fev1()) * 100.0, 1);
        double fvcPct = round((fvcMeas / pred.fvc()) * 100.0, 1);
        double pefPct = pred.pef() > 0 && pefMeas > 0 ? round((pefMeas / pred.pef()) * 100.0, 1) : 0.0;

        // Differential classification of ventilatory defect
        boolean isObstructiveRatio = ratioMeas < 0.70;
        boolean isLowFvc = fvcPct < 80.0;

        ObstructionPattern pattern;
        if (isObstructiveRatio && isLowFvc) {
            pattern = ObstructionPattern.MIXED;
        } else if (isObstructiveRatio) {
            pattern = ObstructionPattern.OBSTRUCTIVE;
        } else if (isLowFvc) {
            pattern = ObstructionPattern.RESTRICTIVE;
        } else {
            pattern = ObstructionPattern.NORMAL;
        }

        // Global Initiative for Chronic Obstructive Lung Disease (GOLD) Staging
        GoldStage goldStage = GoldStage.NOT_APPLICABLE;
        if (pattern == ObstructionPattern.OBSTRUCTIVE || pattern == ObstructionPattern.MIXED) {
            if (fev1Pct >= 80.0) {
                goldStage = GoldStage.GOLD_1_MILD;
            } else if (fev1Pct >= 50.0) {
                goldStage = GoldStage.GOLD_2_MODERATE;
            } else if (fev1Pct >= 30.0) {
                goldStage = GoldStage.GOLD_3_SEVERE;
            } else {
                goldStage = GoldStage.GOLD_4_VERY_SEVERE;
            }
        }

        String severityDesc = determineSeverityDescription(pattern, goldStage, fev1Pct, fvcPct);
        String clinicalInterpretation = buildClinicalInterpretation(patient, pattern, goldStage, fev1Pct, ratioMeas);

        LungFunctionAnalysisResponse response = new LungFunctionAnalysisResponse();
        response.setPatientId(patient.getId());
        response.setPatientName(patient.getFullName());
        response.setAge(patient.getAge());
        response.setHeightCm(patient.getHeightCm());

        response.setFev1Measured(fev1Meas);
        response.setFvcMeasured(fvcMeas);
        response.setFev1FvcRatioMeasured(ratioMeas);
        response.setPefMeasured(pefMeas);

        response.setFev1Predicted(pred.fev1());
        response.setFvcPredicted(pred.fvc());
        response.setFev1FvcRatioPredicted(pred.fev1FvcRatio());
        response.setPefPredicted(pred.pef());

        response.setFev1PercentPredicted(fev1Pct);
        response.setFvcPercentPredicted(fvcPct);
        response.setPefPercentPredicted(pefPct);

        response.setObstructionPattern(pattern);
        response.setGoldStage(goldStage);
        response.setSeverityDescription(severityDesc);
        response.setClinicalInterpretation(clinicalInterpretation);

        // Longitudinal trend evaluation against earliest/established baseline
        if (baseline != null && baseline.getFev1() != null && !baseline.getId().equals(reading.getId())) {
            double delta = round(((fev1Meas - baseline.getFev1()) / baseline.getFev1()) * 100.0, 1);
            response.setFev1ChangeFromBaselinePercent(delta);
            if (delta < -12.0) {
                // An FEV1 drop exceeding 12% and 200mL indicates significant acute decline under ATS criteria
                response.setTrendDirection("RAPID_DECLINE");
            } else if (delta < -5.0) {
                response.setTrendDirection("DECLINING");
            } else if (delta > 5.0) {
                response.setTrendDirection("IMPROVING");
            } else {
                response.setTrendDirection("STABLE");
            }
        } else {
            response.setFev1ChangeFromBaselinePercent(0.0);
            response.setTrendDirection("BASELINE");
        }

        return response;
    }

    private String determineSeverityDescription(ObstructionPattern pattern, GoldStage goldStage, double fev1Pct, double fvcPct) {
        return switch (pattern) {
            case NORMAL -> "Normal spirometric profile. FEV1/FVC ratio and vital capacity are within normal limits (≥ 80% predicted).";
            case OBSTRUCTIVE -> "Airflow obstruction confirmed (FEV1/FVC < 0.70). Clinical classification: " + goldStage.getDescription();
            case RESTRICTIVE -> String.format("Restrictive ventilatory pattern. Preserved FEV1/FVC with reduced vital capacity (FVC at %s%% of predicted).", fvcPct);
            case MIXED -> String.format("Mixed ventilatory defect with concurrent airflow limitation and reduced vital capacity (FVC at %s%% of predicted).", fvcPct);
        };
    }

    private String buildClinicalInterpretation(Patient patient, ObstructionPattern pattern, GoldStage goldStage, double fev1Pct, double ratio) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Patient %s (Age %d) demonstrates ", patient.getFullName(), patient.getAge()));

        String dx = patient.getPrimaryDiagnosis() != null ? patient.getPrimaryDiagnosis().toUpperCase() : "UNSPECIFIED";

        if (pattern == ObstructionPattern.NORMAL) {
            sb.append("normal mechanics with well-preserved ventilatory reserve. No evidence of significant expiratory airflow limitation.");
        } else if (pattern == ObstructionPattern.OBSTRUCTIVE) {
            sb.append(String.format("an obstructive defect with an FEV1/FVC ratio of %.2f (below 0.70 threshold) and FEV1 at %.1f%% of predicted (%s). ",
                    ratio, fev1Pct, goldStage.name()));
            if (dx.contains("COPD")) {
                sb.append("Findings are consistent with chronic obstructive pulmonary disease staging; review current maintenance bronchodilator regimen.");
            } else if (dx.contains("ASTHMA")) {
                sb.append("Suggestive of active airway bronchospasm or sub-optimally controlled asthma; post-bronchodilator reversibility is advised.");
            } else {
                sb.append("Clinical correlation with occupational exposures and smoking history is indicated.");
            }
        } else if (pattern == ObstructionPattern.RESTRICTIVE) {
            sb.append("a restrictive pattern. Body plethysmography for total lung capacity (TLC) and diffusing capacity (DLCO) is recommended to rule out parenchymal interstitial lung disease.");
        } else {
            sb.append("a combined mixed obstructive and restrictive defect reflecting substantial pulmonary impairment. Recommend comprehensive pulmonary consult.");
        }

        return sb.toString();
    }

    private double round(double value, int places) {
        if (Double.isNaN(value) || Double.isInfinite(value)) return 0.0;
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
