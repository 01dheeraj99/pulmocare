package com.lunghealth.monitoring.dto.response;

import com.lunghealth.monitoring.model.entity.HealthAlert;
import com.lunghealth.monitoring.model.enums.AlertType;
import com.lunghealth.monitoring.model.enums.RiskLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Outbound clinical payload conveying emergency alerts and provider acknowledgment status.
 */
@Schema(description = "Emergency pulmonary health alert notification payload")
public class HealthAlertResponse {

    @Schema(description = "Alert database ID", example = "1")
    private Long id;

    @Schema(description = "Affected patient database ID", example = "3")
    private Long patientId;

    @Schema(description = "Patient full legal name", example = "Marcus Vance")
    private String patientName;

    @Schema(description = "Timestamp when the emergency condition was detected")
    private LocalDateTime timestamp;

    @Schema(description = "Alert urgency severity tier", example = "CRITICAL")
    private RiskLevel severity;

    @Schema(description = "Clinical alert category", example = "SEVERE_HYPOXEMIA")
    private AlertType alertType;

    @Schema(description = "Diagnostic description and recommended immediate action", example = "CRITICAL HYPOXEMIA ALERT: Oxygen saturation at 86.0% (below critical 85% safety boundary)...")
    private String message;

    @Schema(description = "Whether a clinician has acknowledged this alert", example = "false")
    private Boolean acknowledged;

    @Schema(description = "Timestamp of clinician review and acknowledgment")
    private LocalDateTime acknowledgedAt;

    public static HealthAlertResponse fromEntity(HealthAlert alert) {
        if (alert == null) return null;
        HealthAlertResponse resp = new HealthAlertResponse();
        resp.setId(alert.getId());
        if (alert.getPatient() != null) {
            resp.setPatientId(alert.getPatient().getId());
            resp.setPatientName(alert.getPatient().getFullName());
        }
        resp.setTimestamp(alert.getTimestamp());
        resp.setSeverity(alert.getSeverity());
        resp.setAlertType(alert.getAlertType());
        resp.setMessage(alert.getMessage());
        resp.setAcknowledged(alert.getAcknowledged());
        resp.setAcknowledgedAt(alert.getAcknowledgedAt());
        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public RiskLevel getSeverity() { return severity; }
    public void setSeverity(RiskLevel severity) { this.severity = severity; }

    public AlertType getAlertType() { return alertType; }
    public void setAlertType(AlertType alertType) { this.alertType = alertType; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Boolean getAcknowledged() { return acknowledged; }
    public void setAcknowledged(Boolean acknowledged) { this.acknowledged = acknowledged; }

    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }
}
