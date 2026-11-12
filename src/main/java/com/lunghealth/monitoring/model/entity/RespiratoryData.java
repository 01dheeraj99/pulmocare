package com.lunghealth.monitoring.model.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Clinical entity encapsulating spirometry test results and respiratory vital signs.
 *
 * <p>Stores objective physiological measurements collected via connected spirometers, pulse oximeters,
 * and clinical observation sessions. Includes ATS/ERS spirometric markers (FEV1, FVC, PEF), automated
 * percentage-predicted calculations based on demographic norms, oxygen saturation, and environmental
 * exposure factors.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@Entity
@Table(name = "respiratory_data", indexes = {
    @Index(name = "idx_resp_patient_time", columnList = "patient_id, recordedAt")
})
public class RespiratoryData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Associated patient whose pulmonary telemetry is recorded. */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    /** Timestamp of the spirometry or physiological observation session. */
    @Column(nullable = false)
    private LocalDateTime recordedAt;

    /** Forced Expiratory Volume in the first second (FEV1) in liters (L). */
    @Column(nullable = false)
    private Double fev1;

    /** Forced Vital Capacity (FVC) in liters (L) forcibly exhaled from total lung capacity. */
    @Column(nullable = false)
    private Double fvc;

    /** Calculated FEV1/FVC ratio (Tiffeneau-Pinelli index), decimal representation (e.g. 0.72 = 72%). */
    @Column(nullable = false)
    private Double fev1FvcRatio;

    /** Peak Expiratory Flow (PEF) in liters per minute (L/min). */
    private Double pef;

    /** Percentage of predicted normal FEV1 according to ATS/ERS reference equations (% predicted). */
    private Double fev1PredictedPercent;

    /** Percentage of predicted normal FVC according to ATS/ERS reference equations (% predicted). */
    private Double fvcPredictedPercent;

    /** Percentage of predicted normal PEF according to ATS/ERS reference equations (% predicted). */
    private Double pefPredictedPercent;

    /** Peripheral capillary oxygen saturation measured by pulse oximetry (SpO2 percentage, 50.0 - 100.0%). */
    @Column(nullable = false)
    private Double spo2;

    /** Respiratory rate in breaths per minute (bpm). */
    @Column(nullable = false)
    private Integer respiratoryRate;

    /** Concurrent resting heart rate in beats per minute (bpm). */
    private Integer heartRate;

    /** Core or tympanic body temperature in degrees Celsius (°C). */
    private Double bodyTemperatureCelsius;

    /** Ambient Air Quality Index (AQI) particulate level during testing (0-500 scale). */
    private Integer aqiIndex;

    /** Indicates whether short-acting bronchodilator (e.g., Albuterol 400 mcg) was administered pre-test. */
    private Boolean inhalerUsedPreTest = false;

    /** Clinical context of measurement (e.g., RESTING_BASELINE, POST_BRONCHODILATOR, POST_EXERTION). */
    private String measurementContext;

    /** Clinician observations, patient effort notes, or maneuver acceptability remarks. */
    private String notes;

    public RespiratoryData() {}

    @PrePersist
    protected void onCreate() {
        if (this.recordedAt == null) {
            this.recordedAt = LocalDateTime.now();
        }
        if (this.fev1 != null && this.fvc != null && this.fvc > 0) {
            this.fev1FvcRatio = Math.round((this.fev1 / this.fvc) * 1000.0) / 1000.0;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Patient getPatient() { return patient; }
    public void setPatient(Patient patient) { this.patient = patient; }

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
