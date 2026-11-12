package com.lunghealth.monitoring.model.entity;

import com.lunghealth.monitoring.model.enums.CoughType;
import com.lunghealth.monitoring.model.enums.SputumColor;
import com.lunghealth.monitoring.model.enums.SymptomSeverity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Clinical entity logging patient-reported respiratory symptoms and functional limitations.
 *
 * <p>Tracks standardized respiratory scores including the modified Medical Research Council (mMRC)
 * Dyspnea Scale, Anthonisen sputum purulence indicators, cough characterization, wheezing,
 * nocturnal awakenings, and fever. Automatically maintains a weighted composite symptom score (0–30)
 * to stratify symptom exacerbation risk.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@Entity
@Table(name = "symptom_logs", indexes = {
    @Index(name = "idx_symptom_patient_time", columnList = "patient_id, recordedAt")
})
public class SymptomLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Associated patient for whom symptoms are logged. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /** Timestamp of symptom occurrence or patient reporting session. */
    @Column(nullable = false)
    private LocalDateTime recordedAt;

    /** Graded cough severity (NONE, MILD, MODERATE, SEVERE). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SymptomSeverity coughSeverity = SymptomSeverity.NONE;

    /** Cough quality and presentation (DRY, PRODUCTIVE, or critical HEMOPTYSIS). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CoughType coughType = CoughType.NONE;

    /**
     * Modified Medical Research Council (mMRC) Dyspnea Scale grade (0–4):
     * 0: Breathless only with strenuous exercise
     * 1: Short of breath when hurrying on the level or walking up a slight hill
     * 2: Walks slower than people of the same age on the level, or stops for breath
     * 3: Stops for breath after walking ~100 meters or after a few minutes on level ground
     * 4: Too breathless to leave the house, or breathless when dressing/undressing
     */
    @Column(nullable = false)
    private Integer shortnessOfBreathScale = 0;

    /** Auscultated or self-reported audible expiratory or inspiratory wheeze. */
    @Column(nullable = false)
    private Boolean wheezingPresent = false;

    /** Subjective retrosternal chest tightness or bronchoconstrictive constriction. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SymptomSeverity chestTightness = SymptomSeverity.NONE;

    /** Sputum expectorate color indicating degree of purulence (Anthonisen exacerbation criteria). */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SputumColor sputumColor = SputumColor.NONE;

    /** Nocturnal awakenings triggered by cough, wheezing, or breathlessness (cardinal GINA indicator). */
    @Column(nullable = false)
    private Boolean nocturnalAwakening = false;

    /** Inability to carry out normal activities of daily living (ADLs) or work. */
    @Column(nullable = false)
    private Boolean activityLimitation = false;

    /** Subjective fever sensation or documented oral temperature &ge; 38.0°C. */
    @Column(nullable = false)
    private Boolean feverPresent = false;

    /** Clinician or patient descriptive remarks, environmental context, or medication compliance. */
    @Column(length = 500)
    private String additionalNotes;

    /** Weighted composite symptom severity score (0.0 to 30.0). */
    @Column(nullable = false)
    private Double totalSymptomScore = 0.0;

    public SymptomLog() {}

    @PrePersist
    protected void onCreate() {
        if (this.recordedAt == null) {
            this.recordedAt = LocalDateTime.now();
        }
        calculateScore();
    }

    @PreUpdate
    protected void onUpdate() {
        calculateScore();
    }

    /**
     * Computes the weighted clinical symptom burden score across all physiological dimensions.
     * Weights prioritize red-flag symptoms such as hemoptysis, severe dyspnea, and purulent sputum.
     */
    public void calculateScore() {
        double score = 0.0;

        if (coughSeverity != null) {
            score += switch (coughSeverity) {
                case NONE -> 0;
                case MILD -> 1;
                case MODERATE -> 2.5;
                case SEVERE -> 4;
            };
        }
        if (coughType == CoughType.HEMOPTYSIS) score += 5;

        if (shortnessOfBreathScale != null) {
            score += shortnessOfBreathScale * 2.0;
        }

        if (Boolean.TRUE.equals(wheezingPresent)) score += 3.0;

        if (chestTightness != null) {
            score += switch (chestTightness) {
                case NONE -> 0;
                case MILD -> 1;
                case MODERATE -> 2;
                case SEVERE -> 3.5;
            };
        }

        if (sputumColor != null) {
            score += switch (sputumColor) {
                case NONE, CLEAR_OR_WHITE -> 0;
                case YELLOW -> 2;
                case GREEN, RUST_BROWN -> 3.5;
                case BLOOD_STAINED -> 5;
            };
        }

        if (Boolean.TRUE.equals(nocturnalAwakening)) score += 3.0;

        if (Boolean.TRUE.equals(activityLimitation)) score += 2.5;

        if (Boolean.TRUE.equals(feverPresent)) score += 2.0;

        this.totalSymptomScore = Math.min(score, 30.0);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public SymptomSeverity getCoughSeverity() { return coughSeverity; }
    public void setCoughSeverity(SymptomSeverity coughSeverity) { this.coughSeverity = coughSeverity; }

    public CoughType getCoughType() { return coughType; }
    public void setCoughType(CoughType coughType) { this.coughType = coughType; }

    public Integer getShortnessOfBreathScale() { return shortnessOfBreathScale; }
    public void setShortnessOfBreathScale(Integer shortnessOfBreathScale) { this.shortnessOfBreathScale = shortnessOfBreathScale; }

    public Boolean getWheezingPresent() { return wheezingPresent; }
    public void setWheezingPresent(Boolean wheezingPresent) { this.wheezingPresent = wheezingPresent; }

    public SymptomSeverity getChestTightness() { return chestTightness; }
    public void setChestTightness(SymptomSeverity chestTightness) { this.chestTightness = chestTightness; }

    public SputumColor getSputumColor() { return sputumColor; }
    public void setSputumColor(SputumColor sputumColor) { this.sputumColor = sputumColor; }

    public Boolean getNocturnalAwakening() { return nocturnalAwakening; }
    public void setNocturnalAwakening(Boolean nocturnalAwakening) { this.nocturnalAwakening = nocturnalAwakening; }

    public Boolean getActivityLimitation() { return activityLimitation; }
    public void setActivityLimitation(Boolean activityLimitation) { this.activityLimitation = activityLimitation; }

    public Boolean getFeverPresent() { return feverPresent; }
    public void setFeverPresent(Boolean feverPresent) { this.feverPresent = feverPresent; }

    public String getAdditionalNotes() { return additionalNotes; }
    public void setAdditionalNotes(String additionalNotes) { this.additionalNotes = additionalNotes; }

    public Double getTotalSymptomScore() { return totalSymptomScore; }
    public void setTotalSymptomScore(Double totalSymptomScore) { this.totalSymptomScore = totalSymptomScore; }
}
