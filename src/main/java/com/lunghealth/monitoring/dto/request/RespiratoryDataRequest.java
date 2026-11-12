package com.lunghealth.monitoring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

/**
 * Inbound clinical telemetry payload encapsulating spirometry and vital sign measurements.
 */
@Schema(description = "Spirometry and respiratory vitals ingestion payload")
public class RespiratoryDataRequest {

    @NotNull(message = "Patient ID is required for telemetry record linkage")
    @Schema(description = "Database ID of the monitored patient", example = "1")
    private Long patientId;

    @Schema(description = "Timestamp of reading acquisition (defaults to current time if omitted)")
    private LocalDateTime recordedAt;

    @NotNull(message = "FEV1 measurement is required")
    @DecimalMin(value = "0.2", message = "FEV1 must be at least 0.2 L (minimum physiological threshold)")
    @DecimalMax(value = "8.0", message = "FEV1 cannot exceed 8.0 L")
    @Schema(description = "Forced Expiratory Volume in 1 second in liters", example = "2.35")
    private Double fev1;

    @NotNull(message = "FVC measurement is required")
    @DecimalMin(value = "0.3", message = "FVC must be at least 0.3 L (minimum physiological threshold)")
    @DecimalMax(value = "10.0", message = "FVC cannot exceed 10.0 L")
    @Schema(description = "Forced Vital Capacity in liters", example = "4.15")
    private Double fvc;

    @DecimalMin(value = "20.0", message = "PEF must be at least 20.0 L/min")
    @DecimalMax(value = "1000.0", message = "PEF cannot exceed 1000.0 L/min")
    @Schema(description = "Peak Expiratory Flow in liters per minute", example = "380.0")
    private Double pef;

    @NotNull(message = "Pulse oximetry SpO2 measurement is required")
    @DecimalMin(value = "50.0", message = "SpO2 must be at least 50.0% (valid oximetry range)")
    @DecimalMax(value = "100.0", message = "SpO2 cannot exceed 100.0%")
    @Schema(description = "Peripheral capillary oxygen saturation percentage", example = "94.5")
    private Double spo2;

    @NotNull(message = "Respiratory rate is required")
    @Min(value = 4, message = "Respiratory rate must be at least 4 breaths per minute")
    @Max(value = 60, message = "Respiratory rate cannot exceed 60 breaths per minute")
    @Schema(description = "Respiratory rate in breaths per minute", example = "20")
    private Integer respiratoryRate;

    @Min(value = 30, message = "Heart rate must be at least 30 bpm")
    @Max(value = 250, message = "Heart rate cannot exceed 250 bpm")
    @Schema(description = "Resting heart rate in beats per minute", example = "78")
    private Integer heartRate;

    @Schema(description = "Body temperature in degrees Celsius", example = "36.8")
    private Double bodyTemperatureCelsius;

    @Min(value = 0, message = "Air Quality Index cannot be negative")
    @Schema(description = "Ambient Air Quality Index during measurement (0-500 scale)", example = "45")
    private Integer aqiIndex;

    @Schema(description = "Indicates whether a bronchodilator (e.g. Albuterol) was administered before this test", example = "false")
    private Boolean inhalerUsedPreTest = false;

    @Schema(description = "Measurement clinical context", example = "RESTING_BASELINE")
    private String measurementContext;

    @Schema(description = "Maneuver quality notes or clinician observations", example = "Maneuver met ATS/ERS repeatability criteria; good patient effort.")
    private String notes;

    public RespiratoryDataRequest() {}

    public Long getPatientId() { return patientId; }
    public void setPatientId(Long patientId) { this.patientId = patientId; }

    public LocalDateTime getRecordedAt() { return recordedAt; }
    public void setRecordedAt(LocalDateTime recordedAt) { this.recordedAt = recordedAt; }

    public Double getFev1() { return fev1; }
    public void setFev1(Double fev1) { this.fev1 = fev1; }

    public Double getFvc() { return fvc; }
    public void setFvc(Double fvc) { this.fvc = fvc; }

    public Double getPef() { return pef; }
    public void setPef(Double pef) { this.pef = pef; }

    public Double getSpo2() { return spo2; }
    public void setSpo2(Double spo2) { this.spo2 = spo2; }

    public Integer getRespiratoryRate() { return respiratoryRate; }
    public void setRespiratoryRate(Integer respiratoryRate) { this.respiratoryRate = respiratoryRate; }

    public Integer getHeartRate() { return heartRate; }
    public void setHeartRate(Integer heartRate) { this.heartRate = heartRate; }

    public Double getBodyTemperatureCelsius() { return bodyTemperatureCelsius; }
    public void setBodyTemperatureCelsius(Double bodyTemperatureCelsius) { this.bodyTemperatureCelsius = bodyTemperatureCelsius; }

    public Integer getAqiIndex() { return aqiIndex; }
    public void setAqiIndex(Integer aqiIndex) { this.aqiIndex = aqiIndex; }

    public Boolean getInhalerUsedPreTest() { return inhalerUsedPreTest; }
    public void setInhalerUsedPreTest(Boolean inhalerUsedPreTest) { this.inhalerUsedPreTest = inhalerUsedPreTest; }

    public String getMeasurementContext() { return measurementContext; }
    public void setMeasurementContext(String measurementContext) { this.measurementContext = measurementContext; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
