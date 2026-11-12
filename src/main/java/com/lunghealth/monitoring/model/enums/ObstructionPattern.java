package com.lunghealth.monitoring.model.enums;

/**
 * Ventilatory defect classification according to ATS/ERS spirometry interpretation guidelines.
 *
 * <p>Identifies whether pulmonary impairment is obstructive (diminished flow rates),
 * restrictive (diminished lung expansion/volume), mixed, or normal.</p>
 */
public enum ObstructionPattern {
    NORMAL("Normal Lung Spirometry (Preserved ratio and vital capacity)"),
    OBSTRUCTIVE("Obstructive Defect (Reduced FEV1/FVC ratio < 0.70)"),
    RESTRICTIVE("Restrictive Pattern (Preserved FEV1/FVC ratio, reduced FVC < 80% predicted)"),
    MIXED("Mixed Obstructive and Restrictive Defect (Reduced ratio and reduced volume)");

    private final String description;

    ObstructionPattern(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
