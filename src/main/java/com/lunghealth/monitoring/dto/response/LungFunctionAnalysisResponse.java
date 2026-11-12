package com.lunghealth.monitoring.dto.response;

import com.lunghealth.monitoring.model.enums.GoldStage;
import com.lunghealth.monitoring.model.enums.ObstructionPattern;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Outbound clinical payload delivering complete ATS/ERS spirometric interpretation and GOLD staging.
 */
@Schema(description = "ATS/ERS spirometry analysis, ventilatory defect classification, and GOLD staging payload")
public class LungFunctionAnalysisResponse {

    @Schema(description = "Monitored patient database ID", example = "1")
    private Long patientId;

    @Schema(description = "Patient full legal name", example = "Arthur Pendelton")
    private String patientName;

    @Schema(description = "Chronological age at examination", example = "68")
    private Integer age;

    @Schema(description = "Standing height in centimeters", example = "176.5")
    private Double heightCm;

    @Schema(description = "Measured Forced Expiratory Volume in 1s (L)", example = "2.35")
    private Double fev1Measured;

    @Schema(description = "Measured Forced Vital Capacity (L)", example = "4.15")
    private Double fvcMeasured;

    @Schema(description = "Measured FEV1/FVC ratio (Tiffeneau-Pinelli index)", example = "0.566")
    private Double fev1FvcRatioMeasured;

    @Schema(description = "Measured Peak Expiratory Flow (L/min)", example = "380.0")
    private Double pefMeasured;

    @Schema(description = "Predicted normal FEV1 for demographics (L)", example = "3.43")
    private Double fev1Predicted;

    @Schema(description = "Predicted normal FVC for demographics (L)", example = "4.49")
    private Double fvcPredicted;

    @Schema(description = "Predicted normal FEV1/FVC ratio", example = "0.764")
    private Double fev1FvcRatioPredicted;

    @Schema(description = "Predicted normal Peak Expiratory Flow (L/min)", example = "500.0")
    private Double pefPredicted;

    @Schema(description = "FEV1 % of predicted normal value", example = "68.5")
    private Double fev1PercentPredicted;

    @Schema(description = "FVC % of predicted normal value", example = "92.4")
    private Double fvcPercentPredicted;

    @Schema(description = "PEF % of predicted normal value", example = "76.0")
    private Double pefPercentPredicted;

    @Schema(description = "Ventilatory defect pattern (NORMAL, OBSTRUCTIVE, RESTRICTIVE, MIXED)", example = "OBSTRUCTIVE")
    private ObstructionPattern obstructionPattern;

    @Schema(description = "COPD staging under GOLD criteria", example = "GOLD_2_MODERATE")
    private GoldStage goldStage;

    @Schema(description = "Severity description", example = "Moderate Obstruction (FEV1 50-79% predicted)")
    private String severityDescription;

    @Schema(description = "Clinician interpretive note", example = "Spirometry demonstrates persistent airflow limitation consistent with GOLD 2 COPD.")
    private String clinicalInterpretation;

    @Schema(description = "Percentage change in FEV1 compared to patient baseline", example = "-4.2")
    private Double fev1ChangeFromBaselinePercent;

    @Schema(description = "Longitudinal trajectory vector (STABLE, RAPID_DECLINE, IMPROVED)", example = "STABLE")
    private String trendDirection;

    public LungFunctionAnalysisResponse() {}

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getFev1Measured() { return fev1Measured; }
    public void setFev1Measured(Double fev1Measured) { this.fev1Measured = fev1Measured; }

    public Double getFvcMeasured() { return fvcMeasured; }
    public void setFvcMeasured(Double fvcMeasured) { this.fvcMeasured = fvcMeasured; }

    public Double getFev1FvcRatioMeasured() { return fev1FvcRatioMeasured; }
    public void setFev1FvcRatioMeasured(Double fev1FvcRatioMeasured) { this.fev1FvcRatioMeasured = fev1FvcRatioMeasured; }

    public Double getPefMeasured() { return pefMeasured; }
    public void setPefMeasured(Double pefMeasured) { this.pefMeasured = pefMeasured; }

    public Double getFev1Predicted() { return fev1Predicted; }
    public void setFev1Predicted(Double fev1Predicted) { this.fev1Predicted = fev1Predicted; }

    public Double getFvcPredicted() { return fvcPredicted; }
    public void setFvcPredicted(Double fvcPredicted) { this.fvcPredicted = fvcPredicted; }

    public Double getFev1FvcRatioPredicted() { return fev1FvcRatioPredicted; }
    public void setFev1FvcRatioPredicted(Double fev1FvcRatioPredicted) { this.fev1FvcRatioPredicted = fev1FvcRatioPredicted; }

    public Double getPefPredicted() { return pefPredicted; }
    public void setPefPredicted(Double pefPredicted) { this.pefPredicted = pefPredicted; }

    public Double getFev1PercentPredicted() { return fev1PercentPredicted; }
    public void setFev1PercentPredicted(Double fev1PercentPredicted) { this.fev1PercentPredicted = fev1PercentPredicted; }

    public Double getFvcPercentPredicted() { return fvcPercentPredicted; }
    public void setFvcPercentPredicted(Double fvcPercentPredicted) { this.fvcPercentPredicted = fvcPercentPredicted; }

    public Double getPefPercentPredicted() { return pefPercentPredicted; }
    public void setPefPercentPredicted(Double pefPercentPredicted) { this.pefPercentPredicted = pefPercentPredicted; }

    public ObstructionPattern getObstructionPattern() { return obstructionPattern; }
    public void setObstructionPattern(ObstructionPattern obstructionPattern) { this.obstructionPattern = obstructionPattern; }

    public GoldStage getGoldStage() { return goldStage; }
    public void setGoldStage(GoldStage goldStage) { this.goldStage = goldStage; }

    public String getSeverityDescription() { return severityDescription; }
    public void setSeverityDescription(String severityDescription) { this.severityDescription = severityDescription; }

    public String getClinicalInterpretation() { return clinicalInterpretation; }
    public void setClinicalInterpretation(String clinicalInterpretation) { this.clinicalInterpretation = clinicalInterpretation; }

    public Double getFev1ChangeFromBaselinePercent() { return fev1ChangeFromBaselinePercent; }
    public void setFev1ChangeFromBaselinePercent(Double fev1ChangeFromBaselinePercent) { this.fev1ChangeFromBaselinePercent = fev1ChangeFromBaselinePercent; }

    public String getTrendDirection() { return trendDirection; }
    public void setTrendDirection(String trendDirection) { this.trendDirection = trendDirection; }
}
