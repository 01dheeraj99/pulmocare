package com.lunghealth.monitoring.model.enums;

/**
 * Diagnostic event category for clinical decision support and rapid provider notification.
 */
public enum AlertType {
    SEVERE_HYPOXEMIA("Critical hypoxemia (SpO2 < 85%) requiring urgent oxygenation"),
    TACHYPNEA_WARNING("Tachypneic respiratory rate (> 28 bpm) indicating ventilatory distress"),
    BRADYPNEA_WARNING("Bradypnea (< 8 bpm) indicating respiratory muscle fatigue or depression"),
    ACUTE_AIRFLOW_OBSTRUCTION("Rapid decline in FEV1 (> 12% drop from baseline spirometry)"),
    COPD_EXACERBATION_RISK("Anthonisen Type I/II COPD exacerbation cluster detected"),
    ASTHMA_FLARE_UP("Acute bronchospasm flare with wheeze and nocturnal awakenings"),
    HEMOPTYSIS_ALERT("Hemoptysis reported - urgent pulmonary diagnostic evaluation required"),
    PURULENT_INFECTION_SUSPECTED("Bacterial tracheobronchial infection suspected with febrile response");

    private final String clinicalContext;

    AlertType(String clinicalContext) {
        this.clinicalContext = clinicalContext;
    }

    public String getClinicalContext() {
        return clinicalContext;
    }
}
