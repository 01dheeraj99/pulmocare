package com.lunghealth.monitoring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lunghealth.monitoring.dto.request.PatientRequest;
import com.lunghealth.monitoring.model.enums.BiologicalSex;
import com.lunghealth.monitoring.model.enums.SmokingStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/patients - Should successfully register new patient and return 201 Created")
    void testRegisterPatientSuccess() throws Exception {
        PatientRequest request = new PatientRequest();
        request.setMedicalRecordNumber("TEST-MRN-999");
        request.setFullName("Alice Clinical Test");
        request.setDateOfBirth(LocalDate.of(1985, 6, 15));
        request.setSex(BiologicalSex.FEMALE);
        request.setHeightCm(168.0);
        request.setWeightKg(62.0);
        request.setSmokingStatus(SmokingStatus.NEVER_SMOKER);
        request.setPackYears(0.0);
        request.setPrimaryDiagnosis("ASTHMA");

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.medicalRecordNumber", is("TEST-MRN-999")))
                .andExpect(jsonPath("$.fullName", is("Alice Clinical Test")))
                .andExpect(jsonPath("$.bmi", is(22.0)));
    }

    @Test
    @DisplayName("POST /api/patients - Duplicate MRN should fail with 400 Bad Request")
    void testRegisterDuplicateMrn() throws Exception {
        PatientRequest request = new PatientRequest();
        request.setMedicalRecordNumber("MRN-2026-001");
        request.setFullName("Duplicate Arthur");
        request.setDateOfBirth(LocalDate.of(1960, 1, 1));
        request.setSex(BiologicalSex.MALE);
        request.setHeightCm(170.0);
        request.setWeightKg(70.0);
        request.setSmokingStatus(SmokingStatus.NEVER_SMOKER);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("already registered")));
    }

    @Test
    @DisplayName("GET /api/patients - Should return seeded sample patient cohort")
    void testGetAllPatients() throws Exception {
        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(4))))
                .andExpect(jsonPath("$[0].fullName").exists());
    }

    @Test
    @DisplayName("GET /api/patients/mrn/{mrn} - Should locate patient by MRN")
    void testGetPatientByMrn() throws Exception {
        mockMvc.perform(get("/api/patients/mrn/MRN-2026-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is("Arthur Pendelton")))
                .andExpect(jsonPath("$.primaryDiagnosis", is("COPD")));
    }
}
