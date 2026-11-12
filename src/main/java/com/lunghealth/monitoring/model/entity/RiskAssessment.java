package com.lunghealth.monitoring.model.entity;

import com.lunghealth.monitoring.model.enums.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Clinical entity representing a synthesized multi-factor pulmonary risk assessment.
 *
 * <p>Synthesizes objective respiratory telemetry, spirometric airflow limitation, patient-reported
 * symptom burden, and demographic exposure factors. Evaluates hypoxemia severity, GOLD COPD stage,
 * GINA asthma control status, generates evidence-based clinical recommendations, and flags urgent
 * clinical intervention requirements.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@Entity
@Table(name = "risk_assessments", indexes = {
    @Index(name = "idx_risk_patient_time", columnList = "patient_id, assessedAt")
})
public class RiskAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Patient subject of this clinical risk evaluation. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /** Timestamp when the algorithmic risk evaluation was performed. */
    @Column(nullable = false)
    private LocalDateTime assessedAt;

    /** Overall composite pulmonary risk score (0.0 = minimal risk to 100.0 = extreme risk). */
    @Column(nullable = false)
    private Double overallRiskScore;

    /** Stratified risk tier (LOW, MODERATE, HIGH, CRITICAL). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RiskLevel riskLevel;

    /** Evaluated GOLD COPD stage if airflow obstruction is present. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GoldStage goldStage = GoldStage.NOT_APPLICABLE;

    /** Evaluated GINA asthma symptom control classification. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AsthmaControlLevel asthmaControlLevel = AsthmaControlLevel.NOT_APPLICABLE;

    /** Categorized hypoxemia severity based on SpO2 measurement. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private HypoxemiaSeverity hypoxemiaSeverity = HypoxemiaSeverity.NORMAL;

    /** Spirometric ventilatory pattern (NORMAL, OBSTRUCTIVE, RESTRICTIVE, MIXED). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ObstructionPattern obstructionPattern = ObstructionPattern.NORMAL;

    /** Semicolon-delimited summary of identified physiological and symptomatic risk drivers. */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String riskFactors;

    /** Evidence-based physician guidance notes, diagnostic actions, and pharmacotherapeutic recommendations. */
    @Lob
    @Column(columnDefinition = "TEXT")
    private String clinicalRecommendations;

    /** Flag indicating whether critical values require immediate triage or emergency dispatch. */
    @Column(nullable = false)
    private Boolean requiresImmediateAttention = false;

    /** Reference ID to the evaluated respiratory reading. */
    private Long respiratoryDataId;

    /** Reference ID to the evaluated symptom log. */
    private Long symptomLogId;

    public RiskAssessment() {}

    @PrePersist
    protected void onCreate() {
        if (this.assessedAt == null) {
            this.assessedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

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

    public String getRiskFactors() { return riskFactors; }
    public void setRiskFactors(String riskFactors) { this.riskFactors = riskFactors; }

    public String getClinicalRecommendations() { return clinicalRecommendations; }
    public void setClinicalRecommendations(String clinicalRecommendations) { this.clinicalRecommendations = clinicalRecommendations; }

    public Boolean getRequiresImmediateAttention() { return requiresImmediateAttention; }
    public void setRequiresImmediateAttention(Boolean requiresImmediateAttention) { this.requiresImmediateAttention = requiresImmediateAttention; }

    public Long getRespiratoryDataId() { return respiratoryDataId; }
    public void setRespiratoryDataId(Long respiratoryDataId) { this.respiratoryDataId = respiratoryDataId; }

    public Long getSymptomLogId() { return symptomLogId; }
    public void setSymptomLogId(Long symptomLogId) { this.symptomLogId = symptomLogId; }
}
