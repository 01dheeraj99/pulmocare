package com.lunghealth.monitoring.model.enums;

/**
 * Global Initiative for Asthma (GINA) symptom control staging.
 *
 * <p>Assesses symptom burden based on daytime symptoms, night awakenings, reliever inhaler use,
 * and activity limitation over the preceding assessment window.</p>
 */
public enum AsthmaControlLevel {
    WELL_CONTROLLED("Well-Controlled (Zero GINA risk criteria present)"),
    PARTLY_CONTROLLED("Partly Controlled (1 to 2 GINA risk criteria present)"),
    UNCONTROLLED("Uncontrolled (3 to 4 GINA risk criteria present)"),
    NOT_APPLICABLE("Not Applicable / Non-Asthma Diagnosis");

    private final String description;

    AsthmaControlLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
