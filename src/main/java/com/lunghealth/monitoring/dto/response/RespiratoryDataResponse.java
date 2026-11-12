package com.lunghealth.monitoring.dto.response;

import com.lunghealth.monitoring.model.entity.RespiratoryData;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Outbound clinical payload conveying spirometry maneuvers, vital signs, and predicted percentages.
 */
@Schema(description = "Spirometry and respiratory vitals telemetry payload")
public class RespiratoryDataResponse {

    @Schema(description = "Telemetry record database ID", example = "10")
    private Long id;

    @Schema(description = "Patient database ID", example = "1")
    private Long patientId;

    @Schema(description = "Patient full legal name", example = "Arthur Pendelton")
    private String patientName;

    @Schema(description = "Timestamp when the telemetry was recorded")
    private LocalDateTime recordedAt;

    @Schema(description = "Forced Expiratory Volume in 1 second (L)", example = "2.35")
    private Double fev1;

    @Schema(description = "Forced Vital Capacity (L)", example = "4.15")
    private Double fvc;

    @Schema(description = "FEV1/FVC ratio (Tiffeneau-Pinelli index)", example = "0.566")
    private Double fev1FvcRatio;

    @Schema(description = "Peak Expiratory Flow (L/min)", example = "380.0")
    private Double pef;

    @Schema(description = "FEV1 % of predicted normal value based on ATS/ERS formulas", example = "68.5")
    private Double fev1PredictedPercent;

    @Schema(description = "FVC % of predicted normal value based on ATS/ERS formulas", example = "92.3")
    private Double fvcPredictedPercent;

    @Schema(description = "PEF % of predicted normal value based on ATS/ERS formulas", example = "76.0")
    private Double pefPredictedPercent;

    @Schema(description = "Peripheral capillary oxygen saturation (%)", example = "94.0")
    private Double spo2;

    @Schema(description = "Respiratory rate in breaths per minute", example = "20")
    private Integer respiratoryRate;

    @Schema(description = "Heart rate in beats per minute", example = "78")
    private Integer heartRate;

    @Schema(description = "Core body temperature in Celsius", example = "36.8")
    private Double bodyTemperatureCelsius;

    @Schema(description = "Ambient Air Quality Index", example = "45")
    private Integer aqiIndex;

    @Schema(description = "Whether bronchodilator was administered pre-test", example = "false")
    private Boolean inhalerUsedPreTest;

    @Schema(description = "Clinical measurement context", example = "RESTING_BASELINE")
    private String measurementContext;

    @Schema(description = "Clinician maneuver notes or quality remarks", example = "Good patient effort; verified 3 acceptable curves.")
    private String notes;

    public static RespiratoryDataResponse fromEntity(RespiratoryData entity) {
        if (entity == null) return null;
        RespiratoryDataResponse resp = new RespiratoryDataResponse();
        resp.setId(entity.getId());
        if (entity.getPatient() != null) {
            resp.setPatientId(entity.getPatient().getId());
            resp.setPatientName(entity.getPatient().getFullName());
        }
        resp.setRecordedAt(entity.getRecordedAt());
        resp.setFev1(entity.getFev1());
        resp.setFvc(entity.getFvc());
        resp.setFev1FvcRatio(entity.getFev1FvcRatio());
        resp.setPef(entity.getPef());
        resp.setFev1PredictedPercent(entity.getFev1PredictedPercent());
        resp.setFvcPredictedPercent(entity.getFvcPredictedPercent());
        resp.setPefPredictedPercent(entity.getPefPredictedPercent());
        resp.setSpo2(entity.getSpo2());
        resp.setRespiratoryRate(entity.getRespiratoryRate());
        resp.setHeartRate(entity.getHeartRate());
        resp.setBodyTemperatureCelsius(entity.getBodyTemperatureCelsius());
        resp.setAqiIndex(entity.getAqiIndex());
        resp.setInhalerUsedPreTest(entity.getInhalerUsedPreTest());
        resp.setMeasurementContext(entity.getMeasurementContext());
        resp.setNotes(entity.getNotes());
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

    public Double getFev1() { return fev1; }
    public void setFev1(Double fev1) { this.fev1 = fev1; }

    public Double getFvc() { return fvc; }
    public void setFvc(Double fvc) { this.fvc = fvc; }

    public Double getFev1FvcRatio() { return fev1FvcRatio; }
    public void setFev1FvcRatio(Double fev1FvcRatio) { this.fev1FvcRatio = fev1FvcRatio; }

    public Double getPef() { return pef; }
    public void setPef(Double pef) { this.pef = pef; }

    public Double getFev1PredictedPercent() { return fev1PredictedPercent; }
    public void setFev1PredictedPercent(Double fev1PredictedPercent) { this.fev1PredictedPercent = fev1PredictedPercent; }

    public Double getFvcPredictedPercent() { return fvcPredictedPercent; }
    public void setFvcPredictedPercent(Double fvcPredictedPercent) { this.fvcPredictedPercent = fvcPredictedPercent; }

    public Double getPefPredictedPercent() { return pefPredictedPercent; }
    public void setPefPredictedPercent(Double pefPredictedPercent) { this.pefPredictedPercent = pefPredictedPercent; }

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
