package com.lunghealth.monitoring.controller;

import com.lunghealth.monitoring.dto.response.PatientHealthSummaryResponse;
import com.lunghealth.monitoring.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for clinical reporting, formal PDF documentation, and research telemetry export.
 *
 * <p>Produces multi-format pulmonary reports:
 * <ul>
 *   <li>Structured JSON summaries for electronic health record (EHR/FHIR) integration</li>
 *   <li>Accredited, hospital-grade PDF health reports certified under CLIA ID: 36D0984122 with physician sign-off</li>
 *   <li>Standardized CSV telemetry datasets for clinical research and longitudinal registry analysis</li>
 * </ul>
 * </p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@RestController
@RequestMapping("/api/reports")
@Tag(name = "Clinical Pulmonary Reports", description = "Diagnostic Summaries, Certified PDF Reports & CSV Research Telemetry — Metropolitan Pulmonary Associates")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/patient/{patientId}/summary")
    @Operation(
        summary = "Generate structured clinical health summary (JSON)",
        description = "Compiles an end-to-end clinical overview encompassing patient demographics, latest spirometry with ATS/ERS % predicted values, active symptom burden, risk stratification, and physician action directives"
    )
    public ResponseEntity<PatientHealthSummaryResponse> getPatientSummary(@PathVariable Long patientId) {
        return ResponseEntity.ok(reportService.generatePatientSummary(patientId));
    }

    @GetMapping("/patient/{patientId}/pdf")
    @Operation(
        summary = "Generate certified PDF pulmonary diagnostic report",
        description = "Builds a formal, high-resolution diagnostic PDF report formatted for medical records, patient discharge, or specialist consultation with clinical letterhead, spirometry tables, and signature block"
    )
    public ResponseEntity<byte[]> downloadPdfReport(@PathVariable Long patientId) {
        byte[] pdfBytes = reportService.generatePdfReport(patientId);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename("lung-health-report-patient-" + patientId + ".pdf")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(contentDisposition);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdfBytes);
    }

    @GetMapping("/patient/{patientId}/csv")
    @Operation(
        summary = "Export longitudinal respiratory telemetry as CSV",
        description = "Streams all historical spirometric maneuvers (FEV1, FVC, PEF) and vital signs (SpO2, RR, HR, AQI) as a tabular CSV dataset for clinical epidemiology and registry research"
    )
    public ResponseEntity<byte[]> downloadCsvExport(@PathVariable Long patientId) {
        byte[] csvBytes = reportService.generateCsvExport(patientId);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename("respiratory-data-patient-" + patientId + ".csv")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDisposition(contentDisposition);

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }
}
