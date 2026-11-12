package com.lunghealth.monitoring.model.enums;

/**
 * Composite clinical risk triage tier for respiratory monitoring.
 *
 * <p>Determines clinical follow-up urgency, remote nursing check-ins, or immediate
 * emergency department redirection based on multi-parameter physiological modeling.</p>
 */
public enum RiskLevel {
    LOW("Low Clinical Risk - Physiological parameters stable within baseline ranges"),
    MODERATE("Moderate Risk - Mild desaturation, airflow drop, or increased symptom burden"),
    HIGH("High Risk - Impending exacerbation, significant bronchoconstriction, or infection"),
    CRITICAL("Critical Emergency - Severe hypoxemia, acute respiratory distress, or hemoptysis");

    private final String description;

    RiskLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
