package com.lunghealth.monitoring.dto.request;

import com.lunghealth.monitoring.model.enums.BiologicalSex;
import com.lunghealth.monitoring.model.enums.SmokingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Inbound clinical payload for registering or updating a patient pulmonary profile.
 */
@Schema(description = "Patient registration and pulmonary baseline intake request")
public class PatientRequest {

    @NotBlank(message = "Medical record number is required for clinical record identification")
    @Size(max = 32, message = "MRN cannot exceed 32 characters")
    @Schema(description = "Unique hospital Medical Record Number", example = "MRN-2026-001")
    private String medicalRecordNumber;

    @NotBlank(message = "Full patient legal name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    @Schema(description = "Full legal name", example = "Arthur Pendelton")
    private String fullName;

    @NotNull(message = "Date of birth is required for ATS/ERS spirometric reference calculations")
    @Past(message = "Date of birth must be a past date")
    @Schema(description = "Date of birth", example = "1958-04-12")
    private LocalDate dateOfBirth;

    @NotNull(message = "Biological sex at birth is required for physiological thoracic volume formulas")
    @Schema(description = "Biological sex at birth", example = "MALE")
    private BiologicalSex sex;

    @NotNull(message = "Standing height is required for spirometry prediction regression")
    @DecimalMin(value = "50.0", message = "Height must be at least 50.0 cm")
    @DecimalMax(value = "250.0", message = "Height cannot exceed 250.0 cm")
    @Schema(description = "Standing height in centimeters", example = "176.5")
    private Double heightCm;

    @NotNull(message = "Body weight is required for BMI and chest wall mechanics assessment")
    @DecimalMin(value = "10.0", message = "Weight must be at least 10.0 kg")
    @DecimalMax(value = "300.0", message = "Weight cannot exceed 300.0 kg")
    @Schema(description = "Total body weight in kilograms", example = "78.4")
    private Double weightKg;

    @NotNull(message = "Tobacco smoking status is required for GOLD risk stratification")
    @Schema(description = "Smoking history status", example = "CURRENT_SMOKER")
    private SmokingStatus smokingStatus;

    @Min(value = 0, message = "Cumulative tobacco pack-years cannot be negative")
    @Schema(description = "Cumulative tobacco exposure in pack-years", example = "42.0")
    private Double packYears = 0.0;

    @Schema(description = "Primary pulmonary diagnosis", example = "Chronic Obstructive Pulmonary Disease (COPD)")
    private String primaryDiagnosis;

    @Schema(description = "Occupational airborne particulate or vapor exposures", example = "Commercial carpentry, silica & wood dust (25 yrs)")
    private String occupationalExposure;

    @Schema(description = "Primary emergency caregiver or contact person", example = "Margaret Pendelton")
    private String emergencyContactName;

    @Schema(description = "Emergency caregiver contact telephone", example = "+1-555-019-2831")
    private String emergencyContactPhone;

    public PatientRequest() {}

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
}
