package com.lunghealth.monitoring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

/**
 * Outbound clinical payload delivering aggregated population health metrics and risk triage counts.
 */
@Schema(description = "Population health surveillance and clinical triage distribution payload")
public class DashboardMetricsResponse {

    @Schema(description = "Total registered pulmonary patients", example = "4")
    private long totalPatients;

    @Schema(description = "Total spirometry and vital readings recorded", example = "12")
    private long totalRespiratoryReadings;

    @Schema(description = "Total symptom logs recorded", example = "8")
    private long totalSymptomLogs;

    @Schema(description = "Total unacknowledged active emergency alerts", example = "2")
    private long totalActiveAlerts;

    @Schema(description = "Number of patients currently stratified at CRITICAL risk", example = "1")
    private long criticalRiskPatients;

    @Schema(description = "Number of patients currently stratified at HIGH risk", example = "1")
    private long highRiskPatients;

    @Schema(description = "Cohort mean peripheral oxygen saturation (%)", example = "94.8")
    private Double averageSystemSpo2;

    @Schema(description = "Cohort mean FEV1 volume (L)", example = "2.84")
    private Double averageSystemFev1;

    @Schema(description = "Distribution breakdown across risk tiers (LOW, MODERATE, HIGH, CRITICAL)")
    private Map<String, Long> riskLevelDistribution;

    @Schema(description = "Distribution breakdown across ventilatory defect patterns")
    private Map<String, Long> obstructionPatternDistribution;

    public DashboardMetricsResponse() {}

    public long getTotalPatients() { return totalPatients; }
    public void setTotalPatients(long totalPatients) { this.totalPatients = totalPatients; }

    public long getTotalRespiratoryReadings() { return totalRespiratoryReadings; }
    public void setTotalRespiratoryReadings(long totalRespiratoryReadings) { this.totalRespiratoryReadings = totalRespiratoryReadings; }

    public long getTotalSymptomLogs() { return totalSymptomLogs; }
    public void setTotalSymptomLogs(long totalSymptomLogs) { this.totalSymptomLogs = totalSymptomLogs; }

    public long getTotalActiveAlerts() { return totalActiveAlerts; }
    public void setTotalActiveAlerts(long totalActiveAlerts) { this.totalActiveAlerts = totalActiveAlerts; }

    public long getCriticalRiskPatients() { return criticalRiskPatients; }
    public void setCriticalRiskPatients(long criticalRiskPatients) { this.criticalRiskPatients = criticalRiskPatients; }

    public long getHighRiskPatients() { return highRiskPatients; }
    public void setHighRiskPatients(long highRiskPatients) { this.highRiskPatients = highRiskPatients; }

    public Double getAverageSystemSpo2() { return averageSystemSpo2; }
    public void setAverageSystemSpo2(Double averageSystemSpo2) { this.averageSystemSpo2 = averageSystemSpo2; }

    public Double getAverageSystemFev1() { return averageSystemFev1; }
    public void setAverageSystemFev1(Double averageSystemFev1) { this.averageSystemFev1 = averageSystemFev1; }

    public Map<String, Long> getRiskLevelDistribution() { return riskLevelDistribution; }
    public void setRiskLevelDistribution(Map<String, Long> riskLevelDistribution) { this.riskLevelDistribution = riskLevelDistribution; }

    public Map<String, Long> getObstructionPatternDistribution() { return obstructionPatternDistribution; }
    public void setObstructionPatternDistribution(Map<String, Long> obstructionPatternDistribution) { this.obstructionPatternDistribution = obstructionPatternDistribution; }
}
