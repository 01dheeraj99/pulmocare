package com.lunghealth.monitoring.model.enums;

/**
 * Standard four-tier clinical symptom severity rating.
 */
public enum SymptomSeverity {
    NONE("Asymptomatic / Baseline", 0),
    MILD("Mild - noticeable but does not impede daily activities", 1),
    MODERATE("Moderate - interferes with ordinary physical exertion", 2),
    SEVERE("Severe - disabling, present at rest or minimal effort", 3);

    private final String description;
    private final int rank;

    SymptomSeverity(String description, int rank) {
        this.description = description;
        this.rank = rank;
    }

    public String getDescription() {
        return description;
    }

    public int getRank() {
        return rank;
    }
}
