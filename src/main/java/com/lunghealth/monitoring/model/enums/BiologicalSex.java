package com.lunghealth.monitoring.model.enums;

/**
 * Biological sex at birth.
 *
 * <p>Standard ATS/ERS (American Thoracic Society / European Respiratory Society)
 * spirometry reference algorithms (e.g., Knudson, NHANES III, and GLI) require biological
 * sex at birth to accurately predict physiological thoracic cavity volume, vital capacity,
 * and expiratory airflow dynamics.</p>
 */
public enum BiologicalSex {
    MALE("Male (Knudson/NHANES male thoracic reference equations)"),
    FEMALE("Female (Knudson/NHANES female thoracic reference equations)");

    private final String clinicalContext;

    BiologicalSex(String clinicalContext) {
        this.clinicalContext = clinicalContext;
    }

    public String getClinicalContext() {
        return clinicalContext;
    }
}
