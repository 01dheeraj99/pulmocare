package com.lunghealth.monitoring.model.enums;

/**
 * Patient tobacco exposure classification according to GOLD (Global Initiative for
 * Chronic Obstructive Lung Disease) and CDC pulmonary epidemiology standards.
 */
public enum SmokingStatus {
    NEVER_SMOKER("Never Smoker (<100 lifetime cigarettes)"),
    FORMER_SMOKER("Former Smoker (Quit ≥6 months ago)"),
    CURRENT_SMOKER("Current Smoker (Active tobacco or inhalant user)");

    private final String description;

    SmokingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
