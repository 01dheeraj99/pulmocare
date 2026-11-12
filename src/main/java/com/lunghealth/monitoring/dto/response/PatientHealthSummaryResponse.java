package com.lunghealth.monitoring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Outbound clinical payload compiling a holistic pulmonary health evaluation.
 *
 * <p>Designed for EHR/FHIR interoperability, specialist consultations, and discharge planning.
 * Synthesizes patient demographics, recent spirometry with ATS/ERS norms, current symptom complex,
 * longitudinal trend vectors, and physician guidance notes.</p>
 */
@Schema(description = "Comprehensive pulmonary clinical health report payload")
public class PatientHealthSummaryResponse {

    @Schema(description = "Patient clinical profile")
    private PatientResponse patient;

    @Schema(description = "Report generation timestamp")
    private LocalDateTime generatedAt;

    @Schema(description = "Most recent spirometry and vital telemetry reading")
    private RespiratoryDataResponse latestRespiratoryData;

    @Schema(description = "Most recent respiratory symptom evaluation")
    private SymptomLogResponse latestSymptomLog;

    @Schema(description = "ATS/ERS spirometry interpretation and GOLD staging")
    private LungFunctionAnalysisResponse lungFunctionAnalysis;

    @Schema(description = "Multi-factor clinical risk assessment")
    private RiskAssessmentResponse latestRiskAssessment;

    @Schema(description = "Active unacknowledged emergency alerts")
    private List<HealthAlertResponse> activeAlerts;

    @Schema(description = "Longitudinal spirometry and vital readings")
    private List<RespiratoryDataResponse> longitudinalReadings;

    @Schema(description = "Longitudinal symptom evaluations")
    private List<SymptomLogResponse> longitudinalSymptoms;

    @Schema(description = "Clinician-facing diagnostic executive summary", example = "Patient Arthur Pendelton presents with moderate obstructive ventilatory defect (GOLD 2)...")
    private String executiveSummary;

    @Schema(description = "Evidence-based therapeutic guidance and recommended clinical interventions", example = "Continue dual bronchodilator therapy; initiate smoking cessation counselling...")
    private String physicianGuidanceNotes;

    public PatientHealthSummaryResponse() {}

    public PatientResponse getPatient() { return patient; }
    public void setPatient(PatientResponse patient) { this.patient = patient; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public RespiratoryDataResponse getLatestRespiratoryData() { return latestRespiratoryData; }
    public void setLatestRespiratoryData(RespiratoryDataResponse latestRespiratoryData) { this.latestRespiratoryData = latestRespiratoryData; }

    public SymptomLogResponse getLatestSymptomLog() { return latestSymptomLog; }
    public void setLatestSymptomLog(SymptomLogResponse latestSymptomLog) { this.latestSymptomLog = latestSymptomLog; }

    public LungFunctionAnalysisResponse getLungFunctionAnalysis() { return lungFunctionAnalysis; }
    public void setLungFunctionAnalysis(LungFunctionAnalysisResponse lungFunctionAnalysis) { this.lungFunctionAnalysis = lungFunctionAnalysis; }

    public RiskAssessmentResponse getLatestRiskAssessment() { return latestRiskAssessment; }
    public void setLatestRiskAssessment(RiskAssessmentResponse latestRiskAssessment) { this.latestRiskAssessment = latestRiskAssessment; }

    public List<HealthAlertResponse> getActiveAlerts() { return activeAlerts; }
    public void setActiveAlerts(List<HealthAlertResponse> activeAlerts) { this.activeAlerts = activeAlerts; }

    public List<RespiratoryDataResponse> getLongitudinalReadings() { return longitudinalReadings; }
    public void setLongitudinalReadings(List<RespiratoryDataResponse> longitudinalReadings) { this.longitudinalReadings = longitudinalReadings; }

    public List<SymptomLogResponse> getLongitudinalSymptoms() { return longitudinalSymptoms; }
    public void setLongitudinalSymptoms(List<SymptomLogResponse> longitudinalSymptoms) { this.longitudinalSymptoms = longitudinalSymptoms; }

    public String getExecutiveSummary() { return executiveSummary; }
    public void setExecutiveSummary(String executiveSummary) { this.executiveSummary = executiveSummary; }

    public String getPhysicianGuidanceNotes() { return physicianGuidanceNotes; }
    public void setPhysicianGuidanceNotes(String physicianGuidanceNotes) { this.physicianGuidanceNotes = physicianGuidanceNotes; }
}
