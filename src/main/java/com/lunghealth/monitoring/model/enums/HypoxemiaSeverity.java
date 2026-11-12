package com.lunghealth.monitoring.model.enums;

/**
 * Clinical hypoxemia grading based on peripheral pulse oximetry (SpO2).
 *
 * <p>Calibrated against standard arterial oxygen tension (PaO2) thresholds, where
 * SpO2 &lt; 90% typically reflects PaO2 &lt; 60 mmHg (the steep descent of the oxyhemoglobin
 * dissociation curve).</p>
 */
public enum HypoxemiaSeverity {
    NORMAL("Normal Oxygenation (SpO2 ≥ 95%)"),
    MILD_HYPOXEMIA("Mild Hypoxemia (SpO2 90-94%)"),
    MODERATE_HYPOXEMIA("Moderate Hypoxemia (SpO2 85-89%)"),
    SEVERE_HYPOXEMIA("Severe Hypoxemia / Emergency (SpO2 < 85%)");

    private final String description;

    HypoxemiaSeverity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
