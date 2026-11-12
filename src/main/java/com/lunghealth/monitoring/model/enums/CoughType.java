package com.lunghealth.monitoring.model.enums;

/**
 * Clinical cough presentation and characterization.
 *
 * <p>Differentiates between non-productive irritative coughs, hypersecretory bronchitic
 * productive episodes, and alarming hemoptysis requiring emergency investigation.</p>
 */
public enum CoughType {
    NONE("No active cough reported"),
    DRY("Dry, non-productive irritative or allergic cough"),
    PRODUCTIVE("Productive cough with bronchial secretions/phlegm"),
    HEMOPTYSIS("Hemoptysis (Frank blood or blood-streaked expectoration - URGENT)");

    private final String clinicalDescription;

    CoughType(String clinicalDescription) {
        this.clinicalDescription = clinicalDescription;
    }

    public String getClinicalDescription() {
        return clinicalDescription;
    }
}
