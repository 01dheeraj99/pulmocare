package com.lunghealth.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunghealth.monitoring.dto.request.RespiratoryDataRequest;
import com.lunghealth.monitoring.dto.request.SymptomLogRequest;
import com.lunghealth.monitoring.model.entity.Patient;
import com.lunghealth.monitoring.model.enums.CoughType;
import com.lunghealth.monitoring.model.enums.SputumColor;
import com.lunghealth.monitoring.model.enums.SymptomSeverity;
import com.lunghealth.monitoring.repository.PatientRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RespiratoryAndSymptomApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PatientRepository patientRepository;

    @Test
    @DisplayName("POST /api/respiratory-data - Should ingest telemetry and calculate predicted percentages")
    void testRecordRespiratoryData() throws Exception {
        Patient patient = patientRepository.findByMedicalRecordNumber("MRN-2026-004").orElseThrow();

        RespiratoryDataRequest request = new RespiratoryDataRequest();
        request.setPatientId(patient.getId());
        request.setFev1(3.10);
        request.setFvc(3.75);
        request.setPef(450.0);
        request.setSpo2(98.5);
        request.setRespiratoryRate(16);
        request.setHeartRate(68);
        request.setAqiIndex(35);
        request.setMeasurementContext("POST_EXERCISE");

        mockMvc.perform(post("/api/respiratory-data")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.fev1", is(3.10)))
                .andExpect(jsonPath("$.fev1PredictedPercent", greaterThan(0.0)))
                .andExpect(jsonPath("$.fvcPredictedPercent", greaterThan(0.0)))
                .andExpect(jsonPath("$.fev1FvcRatio", is(0.827)));
    }

    @Test
    @DisplayName("POST /api/symptoms - Should log symptoms and compute composite score")
    void testLogSymptoms() throws Exception {
        Patient patient = patientRepository.findByMedicalRecordNumber("MRN-2026-002").orElseThrow();

        SymptomLogRequest request = new SymptomLogRequest();
        request.setPatientId(patient.getId());
        request.setCoughSeverity(SymptomSeverity.MODERATE);
        request.setCoughType(CoughType.DRY);
        request.setShortnessOfBreathScale(2);
        request.setWheezingPresent(true);
        request.setChestTightness(SymptomSeverity.MILD);
        request.setSputumColor(SputumColor.NONE);
        request.setNocturnalAwakening(true);
        request.setAdditionalNotes("Intermittent wheeze at night");

        mockMvc.perform(post("/api/symptoms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.shortnessOfBreathScale", is(2)))
                .andExpect(jsonPath("$.wheezingPresent", is(true)))
                .andExpect(jsonPath("$.totalSymptomScore", greaterThan(5.0)));
    }

    @Test
    @DisplayName("GET /api/lung-function/patient/{id}/analyze - Should analyze lung function")
    void testAnalyzeLungFunction() throws Exception {
        Patient patient = patientRepository.findByMedicalRecordNumber("MRN-2026-001").orElseThrow();

        mockMvc.perform(get("/api/lung-function/patient/" + patient.getId() + "/analyze"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.obstructionPattern", is("OBSTRUCTIVE")))
                .andExpect(jsonPath("$.goldStage").exists())
                .andExpect(jsonPath("$.fev1PercentPredicted").exists());
    }

    @Test
    @DisplayName("GET /api/alerts/active - Should list unacknowledged alerts")
    void testGetActiveAlerts() throws Exception {
        mockMvc.perform(get("/api/alerts/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].acknowledged", is(false)));
    }
}
