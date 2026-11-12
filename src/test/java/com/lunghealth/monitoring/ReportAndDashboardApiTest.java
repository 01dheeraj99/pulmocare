package com.lunghealth.monitoring;

import com.lunghealth.monitoring.model.entity.Patient;
import com.lunghealth.monitoring.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReportAndDashboardApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    @DisplayName("GET /api/reports/patient/{id}/summary - Should return comprehensive JSON health report")
    void testGetPatientSummaryReport() throws Exception {
        Patient patient = patientRepository.findByMedicalRecordNumber("MRN-2026-003").orElseThrow();

        mockMvc.perform(get("/api/reports/patient/" + patient.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patient.fullName", is("Marcus Vance")))
                .andExpect(jsonPath("$.latestRespiratoryData.spo2", is(86.0)))
                .andExpect(jsonPath("$.latestSymptomLog.coughType", is("HEMOPTYSIS")))
                .andExpect(jsonPath("$.latestRiskAssessment.riskLevel", is("CRITICAL")))
                .andExpect(jsonPath("$.executiveSummary").exists())
                .andExpect(jsonPath("$.physicianGuidanceNotes").exists());
    }

    @Test
    @DisplayName("GET /api/reports/patient/{id}/pdf - Should generate downloadable binary PDF health report")
    void testDownloadPdfReport() throws Exception {
        Patient patient = patientRepository.findByMedicalRecordNumber("MRN-2026-001").orElseThrow();

        byte[] pdfBytes = mockMvc.perform(get("/api/reports/patient/" + patient.getId() + "/pdf"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string("Content-Disposition", containsString("attachment; filename=\"lung-health-report-patient-")))
                .andReturn()
                .getResponse()
                .getContentAsByteArray();

        assertTrue(pdfBytes.length > 1000, "Generated PDF report should be non-empty and well-structured");

        String header = new String(pdfBytes, 0, 5);
        assertTrue(header.startsWith("%PDF"), "Output must be valid PDF format");
    }

    @Test
    @DisplayName("GET /api/reports/patient/{id}/csv - Should export longitudinal CSV telemetry")
    void testDownloadCsvReport() throws Exception {
        Patient patient = patientRepository.findByMedicalRecordNumber("MRN-2026-001").orElseThrow();

        mockMvc.perform(get("/api/reports/patient/" + patient.getId() + "/csv"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/csv"))
                .andExpect(content().string(containsString("PatientID,PatientName,RecordedAt,FEV1_L,FVC_L")))
                .andExpect(content().string(containsString("Arthur Pendelton")));
    }

    @Test
    @DisplayName("GET /api/dashboard/metrics - Should return aggregated system metrics")
    void testGetDashboardMetrics() throws Exception {
        mockMvc.perform(get("/api/dashboard/metrics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPatients", greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.totalRespiratoryReadings", greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$.totalActiveAlerts", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.averageSystemSpo2", greaterThan(80.0)))
                .andExpect(jsonPath("$.riskLevelDistribution").isMap());
    }
}
