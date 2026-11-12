package com.lunghealth.monitoring.dto.response;

import com.lunghealth.monitoring.model.entity.RiskAssessment;
import com.lunghealth.monitoring.model.enums.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Outbound clinical payload conveying multi-factor pulmonary risk stratification results.
 */
@Schema(description = "Multi-factor clinical risk assessment and provider decision support payload")
public class RiskAssessmentResponse {

    @Schema(description = "Risk assessment database ID", example = "3")
    private Long id;

    @Schema(description = "Monitored patient database ID", example = "1")
    private Long patientId;

    @Schema(description = "Patient full legal name", example = "Arthur Pendelton")
    private String patientName;

    @Schema(description = "Timestamp when the risk evaluation was performed")
    private LocalDateTime assessedAt;

    @Schema(description = "Composite pulmonary risk index score (0.0 - 100.0)", example = "46.5")
    private Double overallRiskScore;

    @Schema(description = "Stratified clinical triage tier (LOW, MODERATE, HIGH, CRITICAL)", example = "MODERATE")
    private RiskLevel riskLevel;

    @Schema(description = "Evaluated GOLD COPD stage", example = "GOLD_2_MODERATE")
    private GoldStage goldStage;

    @Schema(description = "Evaluated GINA asthma control classification", example = "NOT_APPLICABLE")
    private AsthmaControlLevel asthmaControlLevel;

    @Schema(description = "Graded hypoxemia severity tier", example = "MILD_HYPOXEMIA")
    private HypoxemiaSeverity hypoxemiaSeverity;

    @Schema(description = "Spirometric ventilatory defect pattern", example = "OBSTRUCTIVE")
    private ObstructionPattern obstructionPattern;

    @Schema(description = "List of identified physiological and symptomatic risk factors")
    private List<String> riskFactors;

    @Schema(description = "Evidence-based clinical decision support guidance directives")
    private List<String> clinicalRecommendations;

    @Schema(description = "Whether patient exhibits red-flag triggers requiring immediate clinical review", example = "false")
    private Boolean requiresImmediateAttention;

    public static RiskAssessmentResponse fromEntity(RiskAssessment entity) {
        if (entity == null) return null;
        RiskAssessmentResponse resp = new RiskAssessmentResponse();
        resp.setId(entity.getId());
        if (entity.getPatient() != null) {
            resp.setPatientId(entity.getPatient().getId());
            resp.setPatientName(entity.getPatient().getFullName());
        }
        resp.setAssessedAt(entity.getAssessedAt());
        resp.setOverallRiskScore(entity.getOverallRiskScore());
        resp.setRiskLevel(entity.getRiskLevel());
        resp.setGoldStage(entity.getGoldStage());
        resp.setAsthmaControlLevel(entity.getAsthmaControlLevel());
        resp.setHypoxemiaSeverity(entity.getHypoxemiaSeverity());
        resp.setObstructionPattern(entity.getObstructionPattern());
        resp.setRequiresImmediateAttention(entity.getRequiresImmediateAttention());

        if (entity.getRiskFactors() != null && !entity.getRiskFactors().isBlank()) {
            resp.setRiskFactors(Arrays.stream(entity.getRiskFactors().split("\\|\\|"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList());
        } else {
            resp.setRiskFactors(Collections.emptyList());
        }

        if (entity.getClinicalRecommendations() != null && !entity.getClinicalRecommendations().isBlank()) {
            resp.setClinicalRecommendations(Arrays.stream(entity.getClinicalRecommendations().split("\\|\\|"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList());
        } else {
            resp.setClinicalRecommendations(Collections.emptyList());
        }

        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public LocalDateTime getAssessedAt() { return assessedAt; }
    public void setAssessedAt(LocalDateTime assessedAt) { this.assessedAt = assessedAt; }

    public Double getOverallRiskScore() { return overallRiskScore; }
    public void setOverallRiskScore(Double overallRiskScore) { this.overallRiskScore = overallRiskScore; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public GoldStage getGoldStage() { return goldStage; }
    public void setGoldStage(GoldStage goldStage) { this.goldStage = goldStage; }

    public AsthmaControlLevel getAsthmaControlLevel() { return asthmaControlLevel; }
    public void setAsthmaControlLevel(AsthmaControlLevel asthmaControlLevel) { this.asthmaControlLevel = asthmaControlLevel; }

    public HypoxemiaSeverity getHypoxemiaSeverity() { return hypoxemiaSeverity; }
    public void setHypoxemiaSeverity(HypoxemiaSeverity hypoxemiaSeverity) { this.hypoxemiaSeverity = hypoxemiaSeverity; }

    public ObstructionPattern getObstructionPattern() { return obstructionPattern; }
    public void setObstructionPattern(ObstructionPattern obstructionPattern) { this.obstructionPattern = obstructionPattern; }

    public List<String> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(List<String> riskFactors) { this.riskFactors = riskFactors; }

    public List<String> getClinicalRecommendations() { return clinicalRecommendations; }
    public void setClinicalRecommendations(List<String> clinicalRecommendations) { this.clinicalRecommendations = clinicalRecommendations; }

    public Boolean getRequiresImmediateAttention() { return requiresImmediateAttention; }
    public void setRequiresImmediateAttention(Boolean requiresImmediateAttention) { this.requiresImmediateAttention = requiresImmediateAttention; }
}
