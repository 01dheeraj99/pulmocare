package com.lunghealth.monitoring.controller;

import com.lunghealth.monitoring.dto.request.SymptomLogRequest;
import com.lunghealth.monitoring.dto.response.SymptomLogResponse;
import com.lunghealth.monitoring.service.SymptomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for logging, tracking, and scoring patient-reported respiratory symptoms.
 *
 * <p>Captures standardized clinical symptom dimensions including the mMRC Dyspnea Scale,
 * Anthonisen sputum purulence progression, cough phenotype (dry vs productive vs hemoptysis),
 * wheezing auscultation, nocturnal awakenings, and fever. Evaluates real-time symptom burden
 * scores (0-30 scale) to detect acute exacerbation onsets early.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@RestController
@RequestMapping("/api/symptoms")
@Tag(name = "Symptom Complex Tracking", description = "Patient Symptom Burden & mMRC Dyspnea Scoring — Metropolitan Pulmonary Associates")
public class SymptomController {

    private final SymptomService symptomService;

    public SymptomController(SymptomService symptomService) {
        this.symptomService = symptomService;
    }

    @PostMapping
    @Operation(
        summary = "Log respiratory symptom complex",
        description = "Ingests patient symptom observations, grades dyspnea on the mMRC 0-4 scale, documents cough phenotype and sputum purulence, and computes an automated symptom burden score"
    )
    public ResponseEntity<SymptomLogResponse> logSymptoms(@Valid @RequestBody SymptomLogRequest request) {
        SymptomLogResponse response = symptomService.logSymptoms(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Retrieve patient symptom history",
        description = "Returns chronologically sorted symptom evaluations to visualize symptom frequency and progression over treatment courses"
    )
    public ResponseEntity<List<SymptomLogResponse>> getPatientSymptoms(@PathVariable Long patientId) {
        return ResponseEntity.ok(symptomService.getPatientSymptoms(patientId));
    }
}
