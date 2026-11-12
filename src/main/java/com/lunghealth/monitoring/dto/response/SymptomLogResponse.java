package com.lunghealth.monitoring.dto.response;

import com.lunghealth.monitoring.model.entity.SymptomLog;
import com.lunghealth.monitoring.model.enums.CoughType;
import com.lunghealth.monitoring.model.enums.SputumColor;
import com.lunghealth.monitoring.model.enums.SymptomSeverity;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Outbound clinical payload conveying patient-reported symptoms, mMRC dyspnea grades, and burden scores.
 */
@Schema(description = "Symptom complex log and functional dyspnea evaluation payload")
public class SymptomLogResponse {

    @Schema(description = "Symptom record database ID", example = "5")
    private Long id;

    @Schema(description = "Patient database ID", example = "1")
    private Long patientId;

    @Schema(description = "Patient full legal name", example = "Arthur Pendelton")
    private String patientName;

    @Schema(description = "Timestamp when symptoms were logged")
    private LocalDateTime recordedAt;

    @Schema(description = "Cough severity (NONE, MILD, MODERATE, SEVERE)", example = "MODERATE")
    private SymptomSeverity coughSeverity;

    @Schema(description = "Cough phenotype (DRY, PRODUCTIVE, HEMOPTYSIS)", example = "PRODUCTIVE")
    private CoughType coughType;

    @Schema(description = "mMRC Dyspnea Scale grade (0-4)", example = "2")
    private Integer shortnessOfBreathScale;

    @Schema(description = "Presence of audible wheezing", example = "true")
    private Boolean wheezingPresent;

    @Schema(description = "Retrosternal chest tightness severity", example = "MILD")
    private SymptomSeverity chestTightness;

    @Schema(description = "Sputum purulence color according to Anthonisen criteria", example = "YELLOW")
    private SputumColor sputumColor;

    @Schema(description = "Whether symptoms triggered nocturnal awakenings", example = "false")
    private Boolean nocturnalAwakening;

    @Schema(description = "Whether symptoms caused functional activity limitation", example = "true")
    private Boolean activityLimitation;

    @Schema(description = "Presence of documented oral fever >= 38.0°C", example = "false")
    private Boolean feverPresent;

    @Schema(description = "Clinician or patient descriptive remarks", example = "Morning productive cough with thick secretions.")
    private String additionalNotes;

    @Schema(description = "Automated composite symptom burden score (0.0 - 30.0)", example = "11.5")
    private Double totalSymptomScore;

    public static SymptomLogResponse fromEntity(SymptomLog entity) {
        if (entity == null) return null;
        SymptomLogResponse resp = new SymptomLogResponse();
        resp.setId(entity.getId());
        if (entity.getPatient() != null) {
            resp.setPatientId(entity.getPatient().getId());
            resp.setPatientName(entity.getPatient().getFullName());
        }
        resp.setRecordedAt(entity.getRecordedAt());
        resp.setCoughSeverity(entity.getCoughSeverity());
        resp.setCoughType(entity.getCoughType());
        resp.setShortnessOfBreathScale(entity.getShortnessOfBreathScale());
        resp.setWheezingPresent(entity.getWheezingPresent());
        resp.setChestTightness(entity.getChestTightness());
        resp.setSputumColor(entity.getSputumColor());
        resp.setNocturnalAwakening(entity.getNocturnalAwakening());
        resp.setActivityLimitation(entity.getActivityLimitation());
        resp.setFeverPresent(entity.getFeverPresent());
        resp.setAdditionalNotes(entity.getAdditionalNotes());
        resp.setTotalSymptomScore(entity.getTotalSymptomScore());
        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

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
