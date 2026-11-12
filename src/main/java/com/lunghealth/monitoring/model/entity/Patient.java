package com.lunghealth.monitoring.model.entity;

import com.lunghealth.monitoring.model.enums.BiologicalSex;
import com.lunghealth.monitoring.model.enums.SmokingStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * Clinical Patient Entity representing a registered individual undergoing pulmonary monitoring.
 *
 * <p>Captures patient demographics, anthropometric measurements essential for ATS/ERS
 * spirometric prediction formulas (age, height, and biological sex), detailed tobacco exposure history
 * (quantified in pack-years), occupational inhalation risks, and primary respiratory diagnoses.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Unique hospital or clinical network Medical Record Number (MRN). */
    @Column(nullable = false, unique = true, length = 32)
    private String medicalRecordNumber;

    /** Full legal patient name for clinical identification. */
    @Column(nullable = false, length = 100)
    private String fullName;

    /** Date of birth used for exact chronological age calculation in ATS/ERS equations. */
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    /** Biological sex at birth, governing thoracic dimension norms in regression models. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private BiologicalSex sex;

    /** Standing height in centimeters, the strongest predictor of vital capacity and thoracic volume. */
    @Column(nullable = false)
    private Double heightCm;

    /** Total body weight in kilograms, used for BMI derivation and mechanical chest wall assessment. */
    @Column(nullable = false)
    private Double weightKg;

    /** Active or historic tobacco smoking categorization. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SmokingStatus smokingStatus;

    /** Cumulative tobacco exposure in pack-years: (packs per day) * (years smoked). */
    @Column(nullable = false)
    private Double packYears = 0.0;

    /** Primary clinical pulmonary diagnosis (e.g., COPD, Bronchial Asthma, Idiopathic Pulmonary Fibrosis). */
    @Column(length = 100)
    private String primaryDiagnosis;

    /** Occupational or environmental particulate inhalation exposure (e.g., silica, grain dust, coal, asbestos). */
    @Column(length = 200)
    private String occupationalExposure;

    /** Designated next-of-kin or emergency caregiver contact name. */
    @Column(length = 100)
    private String emergencyContactName;

    /** Emergency caregiver telephone number for rapid notification during critical alerts. */
    @Column(length = 30)
    private String emergencyContactPhone;

    /** Registration timestamp. */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Timestamp of last profile update. */
    private LocalDateTime updatedAt;

    public Patient() {}

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates the patient's current chronological age in completed years.
     *
     * @return Age in years, or 0 if date of birth is unassigned.
     */
    public int getAge() {
        if (dateOfBirth == null) return 0;
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /**
     * Calculates Body Mass Index (BMI) in kg/m², rounded to one decimal place.
     *
     * @return BMI value, or 0.0 if anthropometric data is incomplete.
     */
    public Double getBmi() {
        if (heightCm == null || weightKg == null || heightCm <= 0) return 0.0;
        double heightInMeters = heightCm / 100.0;
        return Math.round((weightKg / (heightInMeters * heightInMeters)) * 10.0) / 10.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMedicalRecordNumber() { return medicalRecordNumber; }
    public void setMedicalRecordNumber(String medicalRecordNumber) { this.medicalRecordNumber = medicalRecordNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public BiologicalSex getSex() { return sex; }
    public void setSex(BiologicalSex sex) { this.sex = sex; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public SmokingStatus getSmokingStatus() { return smokingStatus; }
    public void setSmokingStatus(SmokingStatus smokingStatus) { this.smokingStatus = smokingStatus; }

    public Double getPackYears() { return packYears; }
    public void setPackYears(Double packYears) { this.packYears = packYears; }

    public String getPrimaryDiagnosis() { return primaryDiagnosis; }
    public void setPrimaryDiagnosis(String primaryDiagnosis) { this.primaryDiagnosis = primaryDiagnosis; }

    public String getOccupationalExposure() { return occupationalExposure; }
    public void setOccupationalExposure(String occupationalExposure) { this.occupationalExposure = occupationalExposure; }

    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }

    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
