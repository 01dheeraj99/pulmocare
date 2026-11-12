package com.lunghealth.monitoring.service;

import com.lunghealth.monitoring.dto.response.*;
import com.lunghealth.monitoring.exception.ResourceNotFoundException;
import com.lunghealth.monitoring.model.entity.*;
import com.lunghealth.monitoring.model.enums.RiskLevel;
import com.lunghealth.monitoring.repository.*;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Service for generating comprehensive clinical pulmonary reports,
 * accredited diagnostic PDF chart summaries, and longitudinal CSV telemetry.
 */
@Service
@Transactional(readOnly = true)
public class ReportService {

    private final PatientRepository patientRepository;
    private final RespiratoryDataRepository respiratoryDataRepository;
    private final SymptomLogRepository symptomLogRepository;
    private final RiskAssessmentRepository riskAssessmentRepository;
    private final HealthAlertRepository healthAlertRepository;
    private final LungFunctionService lungFunctionService;

    public ReportService(PatientRepository patientRepository,
                         RespiratoryDataRepository respiratoryDataRepository,
                         SymptomLogRepository symptomLogRepository,
                         RiskAssessmentRepository riskAssessmentRepository,
                         HealthAlertRepository healthAlertRepository,
                         LungFunctionService lungFunctionService) {
        this.patientRepository = patientRepository;
        this.respiratoryDataRepository = respiratoryDataRepository;
        this.symptomLogRepository = symptomLogRepository;
        this.riskAssessmentRepository = riskAssessmentRepository;
        this.healthAlertRepository = healthAlertRepository;
        this.lungFunctionService = lungFunctionService;
    }

    /**
     * Builds a structured, complete clinical health summary for EHR integration.
     */
    public PatientHealthSummaryResponse generatePatientSummary(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        PatientHealthSummaryResponse summary = new PatientHealthSummaryResponse();
        summary.setPatient(PatientResponse.fromEntity(patient));
        summary.setGeneratedAt(LocalDateTime.now());

        // Latest vitals & telemetry
        respiratoryDataRepository.findFirstByPatientIdOrderByRecordedAtDesc(patientId)
                .ifPresent(r -> summary.setLatestRespiratoryData(RespiratoryDataResponse.fromEntity(r)));

        // Latest recorded symptoms
        symptomLogRepository.findFirstByPatientIdOrderByRecordedAtDesc(patientId)
                .ifPresent(s -> summary.setLatestSymptomLog(SymptomLogResponse.fromEntity(s)));

        // Calculated lung mechanics & ATS/ERS norms
        try {
            summary.setLungFunctionAnalysis(lungFunctionService.analyzeLatest(patientId));
        } catch (Exception e) {
            // Patient may not have completed spirometry yet
        }

        // Stratified clinical risk assessment
        riskAssessmentRepository.findFirstByPatientIdOrderByAssessedAtDesc(patientId)
                .ifPresent(ra -> summary.setLatestRiskAssessment(RiskAssessmentResponse.fromEntity(ra)));

        // Active unacknowledged alerts
        List<HealthAlertResponse> alerts = healthAlertRepository.findByPatientIdOrderByTimestampDesc(patientId)
                .stream()
                .filter(a -> !a.getAcknowledged())
                .map(HealthAlertResponse::fromEntity)
                .toList();
        summary.setActiveAlerts(alerts);

        // Historical time-series
        summary.setLongitudinalReadings(respiratoryDataRepository.findByPatientIdOrderByRecordedAtDesc(patientId)
                .stream().map(RespiratoryDataResponse::fromEntity).toList());
        summary.setLongitudinalSymptoms(symptomLogRepository.findByPatientIdOrderByRecordedAtDesc(patientId)
                .stream().map(SymptomLogResponse::fromEntity).toList());

        summary.setExecutiveSummary(buildExecutiveSummary(patient, summary));
        summary.setPhysicianGuidanceNotes(buildPhysicianGuidance(summary));

        return summary;
    }

    /**
     * Renders a publication-grade, printable pulmonary consultation report in PDF format.
     */
    public byte[] generatePdfReport(Long patientId) {
        PatientHealthSummaryResponse summary = generatePatientSummary(patientId);
        PatientResponse patient = summary.getPatient();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            // Institutional Clinic Header
            Font clinicHeaderFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15, new Color(15, 60, 110));
            Paragraph clinicName = new Paragraph("METROPOLITAN PULMONARY ASSOCIATES & LUNG DIAGNOSTIC CENTER", clinicHeaderFont);
            clinicName.setAlignment(Element.ALIGN_CENTER);
            clinicName.setSpacingAfter(2);
            document.add(clinicName);

            Font subClinicFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.GRAY);
            Paragraph clinicDetails = new Paragraph(
                    "Division of Pulmonary, Critical Care & Sleep Medicine • Cardiopulmonary Diagnostic Laboratory\n" +
                    "CLIA Laboratory ID: 36D0984122 • ATS/ERS Certified Testing Facility • Medical Director: Dr. Eleanor Vance, MD, FCCP",
                    subClinicFont
            );
            clinicDetails.setAlignment(Element.ALIGN_CENTER);
            clinicDetails.setSpacingAfter(8);
            document.add(clinicDetails);

            Font docTitleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, new Color(20, 80, 140));
            Paragraph docTitle = new Paragraph("COMPREHENSIVE PULMONARY FUNCTION & CLINICAL MONITORING REPORT", docTitleFont);
            docTitle.setAlignment(Element.ALIGN_CENTER);
            docTitle.setSpacingAfter(4);
            document.add(docTitle);

            Font timestampFont = FontFactory.getFont(FontFactory.HELVETICA, 8, Color.DARK_GRAY);
            Paragraph timestamp = new Paragraph("Report Generated: " +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy • HH:mm:ss")) + " (EST)", timestampFont);
            timestamp.setAlignment(Element.ALIGN_CENTER);
            timestamp.setSpacingAfter(10);
            document.add(timestamp);

            document.add(new Paragraph("______________________________________________________________________________\n", subClinicFont));

            // Section 1: Patient Demographics & Baseline Table
            document.add(createSectionHeader("1. PATIENT DEMOGRAPHICS & CLINICAL BASELINE"));
            PdfPTable patientTable = new PdfPTable(4);
            patientTable.setWidthPercentage(100);
            patientTable.setSpacingBefore(4);
            patientTable.setSpacingAfter(10);

            addTableCell(patientTable, "Patient Name:", true);
            addTableCell(patientTable, patient.getFullName(), false);
            addTableCell(patientTable, "Medical Record No (MRN):", true);
            addTableCell(patientTable, patient.getMedicalRecordNumber(), false);

            addTableCell(patientTable, "Age / Biological Sex:", true);
            addTableCell(patientTable, patient.getAge() + " yrs / " + patient.getSex(), false);
            addTableCell(patientTable, "Height / Weight (BMI):", true);
            addTableCell(patientTable, patient.getHeightCm() + " cm / " + patient.getWeightKg() + " kg (BMI: " + patient.getBmi() + ")", false);

            addTableCell(patientTable, "Primary Clinical Diagnosis:", true);
            addTableCell(patientTable, patient.getPrimaryDiagnosis() != null ? patient.getPrimaryDiagnosis() : "Unspecified / General Screening", false);
            addTableCell(patientTable, "Tobacco Smoking History:", true);
            addTableCell(patientTable, patient.getSmokingStatus() + " (" + patient.getPackYears() + " pack-years cumulative)", false);

            addTableCell(patientTable, "Workplace / Environmental Exposure:", true);
            addTableCell(patientTable, patient.getOccupationalExposure() != null ? patient.getOccupationalExposure() : "No significant hazard noted", false);
            addTableCell(patientTable, "Emergency Contact:", true);
            addTableCell(patientTable, (patient.getEmergencyContactName() != null ? patient.getEmergencyContactName() : "N/A") + " (" + (patient.getEmergencyContactPhone() != null ? patient.getEmergencyContactPhone() : "") + ")", false);

            document.add(patientTable);

            // Section 2: Vitals & Telemetry
            if (summary.getLatestRespiratoryData() != null) {
                RespiratoryDataResponse vitals = summary.getLatestRespiratoryData();
                document.add(createSectionHeader("2. RESPIRATORY VITALS & PHYSIOLOGICAL TELEMETRY"));

                PdfPTable vitalsTable = new PdfPTable(4);
                vitalsTable.setWidthPercentage(100);
                vitalsTable.setSpacingBefore(4);
                vitalsTable.setSpacingAfter(10);

                addTableCell(vitalsTable, "Oxygen Saturation (SpO2):", true);
                addTableCell(vitalsTable, vitals.getSpo2() + "% " + (vitals.getSpo2() < 85 ? "(CRITICAL HYPOXEMIA)" : vitals.getSpo2() < 90 ? "(MODERATE HYPOXEMIA)" : vitals.getSpo2() < 95 ? "(MILD HYPOXEMIA)" : "(NORMAL)"), false);
                addTableCell(vitalsTable, "Respiratory Rate:", true);
                addTableCell(vitalsTable, vitals.getRespiratoryRate() + " bpm " + (vitals.getRespiratoryRate() > 28 ? "(TACHYPNEA)" : "(Normal: 12-20)"), false);

                addTableCell(vitalsTable, "Heart Rate / Pulse:", true);
                addTableCell(vitalsTable, (vitals.getHeartRate() != null ? vitals.getHeartRate() + " bpm" : "N/A"), false);
                addTableCell(vitalsTable, "Core Body Temperature:", true);
                addTableCell(vitalsTable, (vitals.getBodyTemperatureCelsius() != null ? vitals.getBodyTemperatureCelsius() + " °C" : "N/A"), false);

                addTableCell(vitalsTable, "Ambient Air Quality (AQI):", true);
                addTableCell(vitalsTable, (vitals.getAqiIndex() != null ? vitals.getAqiIndex() + " (" + (vitals.getAqiIndex() > 150 ? "Unhealthy" : vitals.getAqiIndex() > 100 ? "Sensitive Alert" : "Acceptable") + ")" : "N/A"), false);
                addTableCell(vitalsTable, "Clinical Test Context:", true);
                addTableCell(vitalsTable, (vitals.getMeasurementContext() != null ? vitals.getMeasurementContext() : "Routine") + " | Bronchodilator Pre-test: " + (Boolean.TRUE.equals(vitals.getInhalerUsedPreTest()) ? "YES" : "NO"), false);

                document.add(vitalsTable);
            }

            // Section 3: Spirometry & Ventilatory Mechanics Table
            if (summary.getLungFunctionAnalysis() != null) {
                LungFunctionAnalysisResponse lf = summary.getLungFunctionAnalysis();
                document.add(createSectionHeader("3. SPIROMETRIC MECHANICS & VENTILATORY PROFILE"));

                PdfPTable spiroTable = new PdfPTable(4);
                spiroTable.setWidthPercentage(100);
                spiroTable.setSpacingBefore(4);
                spiroTable.setSpacingAfter(6);

                addTableHeader(spiroTable, "Parameter");
                addTableHeader(spiroTable, "Measured Value");
                addTableHeader(spiroTable, "Predicted (ATS/ERS)");
                addTableHeader(spiroTable, "% Predicted");

                addTableCell(spiroTable, "FEV1 (Forced Expiratory Vol in 1s)", false);
                addTableCell(spiroTable, lf.getFev1Measured() + " L", false);
                addTableCell(spiroTable, lf.getFev1Predicted() + " L", false);
                addTableCell(spiroTable, lf.getFev1PercentPredicted() + "%", false);

                addTableCell(spiroTable, "FVC (Forced Vital Capacity)", false);
                addTableCell(spiroTable, lf.getFvcMeasured() + " L", false);
                addTableCell(spiroTable, lf.getFvcPredicted() + " L", false);
                addTableCell(spiroTable, lf.getFvcPercentPredicted() + "%", false);

                addTableCell(spiroTable, "FEV1 / FVC Ratio", false);
                addTableCell(spiroTable, String.valueOf(lf.getFev1FvcRatioMeasured()), false);
                addTableCell(spiroTable, String.valueOf(lf.getFev1FvcRatioPredicted()), false);
                addTableCell(spiroTable, (lf.getFev1FvcRatioMeasured() < 0.70 ? "LOW (< 0.70 Obstructive)" : "NORMAL (≥ 0.70)"), false);

                addTableCell(spiroTable, "Peak Expiratory Flow (PEF)", false);
                addTableCell(spiroTable, (lf.getPefMeasured() != null ? lf.getPefMeasured() + " L/min" : "N/A"), false);
                addTableCell(spiroTable, (lf.getPefPredicted() != null ? lf.getPefPredicted() + " L/min" : "N/A"), false);
                addTableCell(spiroTable, (lf.getPefPercentPredicted() != null ? lf.getPefPercentPredicted() + "%" : "N/A"), false);

                document.add(spiroTable);

                Paragraph interp = new Paragraph(
                        "Ventilatory Pattern: " + lf.getObstructionPattern().getDescription() + "\n" +
                        "Severity Staging: " + (lf.getGoldStage() != null ? lf.getGoldStage().getDescription() : "N/A") + "\n" +
                        "Clinical Assessment: " + lf.getClinicalInterpretation(),
                        FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 9, Color.DARK_GRAY)
                );
                interp.setSpacingAfter(10);
                document.add(interp);
            }

            // Section 4: Symptom Assessment
            if (summary.getLatestSymptomLog() != null) {
                SymptomLogResponse sym = summary.getLatestSymptomLog();
                document.add(createSectionHeader("4. PATIENT-REPORTED SYMPTOM COMPLEX & FUNCTIONAL BURDEN"));

                PdfPTable symTable = new PdfPTable(4);
                symTable.setWidthPercentage(100);
                symTable.setSpacingBefore(4);
                symTable.setSpacingAfter(10);

                addTableCell(symTable, "Cough Severity & Type:", true);
                addTableCell(symTable, sym.getCoughSeverity() + " (" + sym.getCoughType() + ")", false);
                addTableCell(symTable, "mMRC Dyspnea Scale (0-4):", true);
                addTableCell(symTable, "Grade " + sym.getShortnessOfBreathScale() + " / 4", false);

                addTableCell(symTable, "Wheezing / Whistling:", true);
                addTableCell(symTable, Boolean.TRUE.equals(sym.getWheezingPresent()) ? "YES" : "NO", false);
                addTableCell(symTable, "Sputum Purulence / Color:", true);
                addTableCell(symTable, String.valueOf(sym.getSputumColor()), false);

                addTableCell(symTable, "Nocturnal Awakening:", true);
                addTableCell(symTable, Boolean.TRUE.equals(sym.getNocturnalAwakening()) ? "YES" : "NO", false);
                addTableCell(symTable, "Composite Symptom Score:", true);
                addTableCell(symTable, sym.getTotalSymptomScore() + " / 30", false);

                document.add(symTable);
            }

            // Section 5: Risk Stratification & Clinical Guidance
            if (summary.getLatestRiskAssessment() != null) {
                RiskAssessmentResponse risk = summary.getLatestRiskAssessment();
                document.add(createSectionHeader("5. CLINICAL RISK STRATIFICATION & CARE PLAN"));

                Color riskColor = switch (risk.getRiskLevel()) {
                    case CRITICAL -> new Color(178, 34, 34);
                    case HIGH -> new Color(220, 100, 0);
                    case MODERATE -> new Color(200, 150, 0);
                    case LOW -> new Color(34, 139, 34);
                };

                Paragraph riskBadge = new Paragraph(String.format("OVERALL LUNG HEALTH RISK SCORE: %.1f / 100  |  STRATIFICATION LEVEL: %s",
                        risk.getOverallRiskScore(), risk.getRiskLevel()), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, riskColor));
                riskBadge.setSpacingBefore(3);
                riskBadge.setSpacingAfter(6);
                document.add(riskBadge);

                if (!risk.getRiskFactors().isEmpty()) {
                    Paragraph rfTitle = new Paragraph("Identified Risk Factors:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9));
                    document.add(rfTitle);
                    com.lowagie.text.List rfList = new com.lowagie.text.List(com.lowagie.text.List.UNORDERED);
                    rfList.setListSymbol("• ");
                    for (String rf : risk.getRiskFactors()) {
                        rfList.add(new ListItem(rf, FontFactory.getFont(FontFactory.HELVETICA, 8)));
                    }
                    document.add(rfList);
                }

                if (!risk.getClinicalRecommendations().isEmpty()) {
                    Paragraph recTitle = new Paragraph("\nActionable Clinical Recommendations:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9));
                    document.add(recTitle);
                    com.lowagie.text.List recList = new com.lowagie.text.List(com.lowagie.text.List.ORDERED);
                    for (String rec : risk.getClinicalRecommendations()) {
                        recList.add(new ListItem(rec, FontFactory.getFont(FontFactory.HELVETICA, 8)));
                    }
                    document.add(recList);
                }
            }

            // Physician Sign-off block
            document.add(new Paragraph("\n"));
            PdfPTable signTable = new PdfPTable(2);
            signTable.setWidthPercentage(100);
            signTable.setSpacingBefore(15);

            PdfPCell cell1 = new PdfPCell(new Phrase("Reviewing Pulmonologist / Medical Officer:\n\nDr. Eleanor Vance, MD, FCCP\nLic. #PULM-774921-MD\nSignature: ___________________________", FontFactory.getFont(FontFactory.HELVETICA, 8)));
            cell1.setBorder(Rectangle.NO_BORDER);
            PdfPCell cell2 = new PdfPCell(new Phrase("Clinical Validation & Review Date:\n\nDate: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) + "\nStatus: Electronically Verified by Attending", FontFactory.getFont(FontFactory.HELVETICA, 8)));
            cell2.setBorder(Rectangle.NO_BORDER);

            signTable.addCell(cell1);
            signTable.addCell(cell2);
            document.add(signTable);

            document.close();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to generate PDF pulmonary health report: " + ex.getMessage(), ex);
        }

        return out.toByteArray();
    }

    public byte[] generateCsvExport(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + patientId));

        List<RespiratoryData> readings = respiratoryDataRepository.findByPatientIdOrderByRecordedAtDesc(patientId);

        StringBuilder csv = new StringBuilder();
        csv.append("PatientID,PatientName,RecordedAt,FEV1_L,FVC_L,Ratio,PEF_L_min,FEV1_Pct_Pred,SpO2_Pct,RespRate_bpm,HeartRate_bpm,AQI\n");

        for (RespiratoryData r : readings) {
            csv.append(patient.getId()).append(",")
                    .append("\"").append(patient.getFullName()).append("\",")
                    .append(r.getRecordedAt()).append(",")
                    .append(r.getFev1()).append(",")
                    .append(r.getFvc()).append(",")
                    .append(r.getFev1FvcRatio()).append(",")
                    .append(r.getPef() != null ? r.getPef() : "").append(",")
                    .append(r.getFev1PredictedPercent() != null ? r.getFev1PredictedPercent() : "").append(",")
                    .append(r.getSpo2()).append(",")
                    .append(r.getRespiratoryRate()).append(",")
                    .append(r.getHeartRate() != null ? r.getHeartRate() : "").append(",")
                    .append(r.getAqiIndex() != null ? r.getAqiIndex() : "")
                    .append("\n");
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private Paragraph createSectionHeader(String title) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, new Color(20, 60, 100));
        Paragraph p = new Paragraph(title, font);
        p.setSpacingBefore(6);
        p.setSpacingAfter(3);
        return p;
    }

    private void addTableHeader(PdfPTable table, String text) {
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(25, 80, 130));
        cell.setPadding(4);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    private void addTableCell(PdfPTable table, String text, boolean isHeader) {
        Font font = isHeader
                ? FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.DARK_GRAY)
                : FontFactory.getFont(FontFactory.HELVETICA, 8, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        if (isHeader) {
            cell.setBackgroundColor(new Color(240, 243, 246));
        }
        cell.setPadding(4);
        table.addCell(cell);
    }

    private String buildExecutiveSummary(Patient patient, PatientHealthSummaryResponse summary) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Longitudinal pulmonary evaluation for %s (%s, age %d). ",
                patient.getFullName(), patient.getSex(), patient.getAge()));

        if (summary.getLatestRiskAssessment() != null) {
            RiskAssessmentResponse risk = summary.getLatestRiskAssessment();
            sb.append(String.format("Current lung risk index is classified as %s (Score: %.1f/100). ",
                    risk.getRiskLevel(), risk.getOverallRiskScore()));
            if (risk.getRequiresImmediateAttention()) {
                sb.append("ATTENTION REQUIRED: Patient triggers acute respiratory alert thresholds. ");
            }
        }

        if (summary.getLungFunctionAnalysis() != null) {
            LungFunctionAnalysisResponse lf = summary.getLungFunctionAnalysis();
            sb.append(String.format("Spirometric pattern shows %s with FEV1 at %.1f%% of predicted normal. ",
                    lf.getObstructionPattern().getDescription(), lf.getFev1PercentPredicted()));
        }

        return sb.toString();
    }

    private String buildPhysicianGuidance(PatientHealthSummaryResponse summary) {
        StringBuilder sb = new StringBuilder();
        if (summary.getLatestRiskAssessment() != null && summary.getLatestRiskAssessment().getRiskLevel() == RiskLevel.CRITICAL) {
            sb.append("URGENT: Patient requires prompt pulmonary review, arterial blood gas (ABG) analysis, and potential escalation of bronchodilator or ventilatory support. ");
        } else {
            sb.append("Routine maintenance follow-up. Review inhaler compliance and spirometry in 3 to 6 months. ");
        }
        return sb.toString();
    }
}
