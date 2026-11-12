package com.lunghealth.monitoring.dto.request;

import com.lunghealth.monitoring.model.enums.CoughType;
import com.lunghealth.monitoring.model.enums.SputumColor;
import com.lunghealth.monitoring.model.enums.SymptomSeverity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Inbound clinical payload for logging patient respiratory symptoms and functional dyspnea scores.
 */
@Schema(description = "Respiratory symptom and functional impairment logging payload")
public class SymptomLogRequest {

    @NotNull(message = "Patient ID is required for symptom record linkage")
    @Schema(description = "Database ID of the monitored patient", example = "1")
    private Long patientId;

    @Schema(description = "Timestamp of symptom logging (defaults to current time if omitted)")
    private LocalDateTime recordedAt;

    @Schema(description = "Severity of cough", example = "MODERATE")
    private SymptomSeverity coughSeverity = SymptomSeverity.NONE;

    @Schema(description = "Clinical character of cough (DRY, PRODUCTIVE, or HEMOPTYSIS)", example = "DRY")
    private CoughType coughType = CoughType.NONE;

    @Min(value = 0, message = "mMRC dyspnea scale minimum grade is 0")
    @Max(value = 4, message = "mMRC dyspnea scale maximum grade is 4")
    @Schema(description = "Modified Medical Research Council (mMRC) Dyspnea Scale grade (0-4)", example = "2")
    private Integer shortnessOfBreathScale = 0;

    @Schema(description = "Presence of audible expiratory or inspiratory wheeze", example = "true")
    private Boolean wheezingPresent = false;

    @Schema(description = "Severity of retrosternal chest tightness", example = "MILD")
    private SymptomSeverity chestTightness = SymptomSeverity.NONE;

    @Schema(description = "Sputum purulence color according to Anthonisen criteria", example = "NONE")
    private SputumColor sputumColor = SputumColor.NONE;

    @Schema(description = "Whether cough or breathlessness caused nocturnal awakening", example = "true")
    private Boolean nocturnalAwakening = false;

    @Schema(description = "Whether symptoms limited activities of daily living or work", example = "false")
    private Boolean activityLimitation = false;

    @Schema(description = "Documented oral fever >= 38.0°C or chills", example = "false")
    private Boolean feverPresent = false;

    @Schema(description = "Contextual patient remarks, triggers, or therapeutic observations", example = "Intermittent wheeze at night")
    private String additionalNotes;

    public SymptomLogRequest() {}

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

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
}
