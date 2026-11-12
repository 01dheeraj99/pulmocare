package com.lunghealth.monitoring.dto.response;

import com.lunghealth.monitoring.model.entity.Patient;
import com.lunghealth.monitoring.model.enums.BiologicalSex;
import com.lunghealth.monitoring.model.enums.SmokingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Outbound clinical response payload representing a monitored patient's demographic and pulmonary profile.
 */
@Schema(description = "Patient clinical profile and pulmonary demographic baseline")
public class PatientResponse {

    @Schema(description = "Internal database ID", example = "1")
    private Long id;

    @Schema(description = "Hospital Medical Record Number", example = "MRN-2026-001")
    private String medicalRecordNumber;

    @Schema(description = "Full legal name", example = "Arthur Pendelton")
    private String fullName;

    @Schema(description = "Date of birth", example = "1958-04-12")
    private LocalDate dateOfBirth;

    @Schema(description = "Chronological age in years", example = "68")
    private Integer age;

    @Schema(description = "Biological sex at birth", example = "MALE")
    private BiologicalSex sex;

    @Schema(description = "Standing height in centimeters", example = "176.5")
    private Double heightCm;

    @Schema(description = "Body weight in kilograms", example = "78.4")
    private Double weightKg;

    @Schema(description = "Calculated Body Mass Index (BMI in kg/m²)", example = "25.2")
    private Double bmi;

    @Schema(description = "Tobacco smoking status", example = "CURRENT_SMOKER")
    private SmokingStatus smokingStatus;

    @Schema(description = "Cumulative tobacco exposure in pack-years", example = "42.0")
    private Double packYears;

    @Schema(description = "Primary pulmonary diagnosis", example = "COPD")
    private String primaryDiagnosis;

    @Schema(description = "Occupational airborne particulate exposures", example = "Commercial carpentry, wood and silica dust")
    private String occupationalExposure;

    @Schema(description = "Designated emergency caregiver name", example = "Margaret Pendelton")
    private String emergencyContactName;

    @Schema(description = "Emergency caregiver contact telephone", example = "+1-555-019-2831")
    private String emergencyContactPhone;

    @Schema(description = "Enrollment timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Last profile modification timestamp")
    private LocalDateTime updatedAt;

    public static PatientResponse fromEntity(Patient patient) {
        if (patient == null) return null;
        PatientResponse resp = new PatientResponse();
        resp.setId(patient.getId());
        resp.setMedicalRecordNumber(patient.getMedicalRecordNumber());
        resp.setFullName(patient.getFullName());
        resp.setDateOfBirth(patient.getDateOfBirth());
        resp.setAge(patient.getAge());
        resp.setSex(patient.getSex());
        resp.setHeightCm(patient.getHeightCm());
        resp.setWeightKg(patient.getWeightKg());
        resp.setBmi(patient.getBmi());
        resp.setSmokingStatus(patient.getSmokingStatus());
        resp.setPackYears(patient.getPackYears());
        resp.setPrimaryDiagnosis(patient.getPrimaryDiagnosis());
        resp.setOccupationalExposure(patient.getOccupationalExposure());
        resp.setEmergencyContactName(patient.getEmergencyContactName());
        resp.setEmergencyContactPhone(patient.getEmergencyContactPhone());
        resp.setCreatedAt(patient.getCreatedAt());
        resp.setUpdatedAt(patient.getUpdatedAt());
        return resp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMedicalRecordNumber() { return medicalRecordNumber; }
    public void setMedicalRecordNumber(String medicalRecordNumber) { this.medicalRecordNumber = medicalRecordNumber; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public BiologicalSex getSex() { return sex; }
    public void setSex(BiologicalSex sex) { this.sex = sex; }

    public Double getHeightCm() { return heightCm; }
    public void setHeightCm(Double heightCm) { this.heightCm = heightCm; }

    public Double getWeightKg() { return weightKg; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Double getBmi() { return bmi; }
    public void setBmi(Double bmi) { this.bmi = bmi; }

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
