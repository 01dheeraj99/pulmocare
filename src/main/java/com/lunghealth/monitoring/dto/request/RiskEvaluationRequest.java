package com.lunghealth.monitoring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * Inbound clinical payload to trigger on-demand multi-factor pulmonary risk assessment.
 */
@Schema(description = "On-demand pulmonary clinical risk evaluation request")
public class RiskEvaluationRequest {

    @NotNull(message = "Patient ID is required for risk assessment execution")
    @Schema(description = "Database ID of the patient to evaluate", example = "1")
    private Long patientId;

    @Schema(description = "Optional specific respiratory data reading ID to evaluate (defaults to latest)")
    private Long respiratoryDataId;

    @Schema(description = "Optional specific symptom log ID to evaluate (defaults to latest)")
    private Long symptomLogId;

    public RiskEvaluationRequest() {}

    public RiskEvaluationRequest(Long patientId) {
        this.patientId = patientId;
    }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public Long getRespiratoryDataId() { return respiratoryDataId; }
    public void setRespiratoryDataId(Long respiratoryDataId) { this.respiratoryDataId = respiratoryDataId; }

    public Long getSymptomLogId() { return symptomLogId; }
    public void setSymptomLogId(Long symptomLogId) { this.symptomLogId = symptomLogId; }
}
