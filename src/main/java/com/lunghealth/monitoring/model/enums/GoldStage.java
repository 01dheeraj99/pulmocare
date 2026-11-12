package com.lunghealth.monitoring.model.enums;

/**
 * Global Initiative for Chronic Obstructive Lung Disease (GOLD) spirometric classification.
 *
 * <p>Classifies severity of airflow limitation in patients with persistent post-bronchodilator
 * airflow obstruction (FEV1/FVC &lt; 0.70) based on FEV1 % predicted.</p>
 */
public enum GoldStage {
    NOT_APPLICABLE("Not Applicable / No Airflow Obstruction"),
    GOLD_1_MILD("GOLD 1: Mild (FEV1 ≥ 80% predicted)"),
    GOLD_2_MODERATE("GOLD 2: Moderate (50% ≤ FEV1 < 80% predicted)"),
    GOLD_3_SEVERE("GOLD 3: Severe (30% ≤ FEV1 < 50% predicted)"),
    GOLD_4_VERY_SEVERE("GOLD 4: Very Severe (FEV1 < 30% predicted)");

    private final String description;

    GoldStage(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
