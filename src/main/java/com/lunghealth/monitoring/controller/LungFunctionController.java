package com.lunghealth.monitoring.controller;

import com.lunghealth.monitoring.dto.response.LungFunctionAnalysisResponse;
import com.lunghealth.monitoring.service.LungFunctionCalculator;
import com.lunghealth.monitoring.service.LungFunctionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for ATS/ERS spirometric interpretation, ventilatory defect classification, and GOLD staging.
 *
 * <p>Implements verified Knudson and NHANES III regression algorithms to evaluate airflow limitation,
 * classify ventilatory impairment into Normal, Obstructive, Restrictive, or Mixed patterns, stratify
 * COPD severity under GOLD 1-4 criteria, and calculate longitudinal decline velocity to warn of rapid FEV1 loss.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@RestController
@RequestMapping("/api/lung-function")
@Tag(name = "Lung Function & Spirometry Interpretation", description = "ATS/ERS Reference Algorithms, GOLD Staging & Trajectory Analysis — Metropolitan Pulmonary Associates")
public class LungFunctionController {

    private final LungFunctionService lungFunctionService;

    public LungFunctionController(LungFunctionService lungFunctionService) {
        this.lungFunctionService = lungFunctionService;
    }

    @GetMapping("/patient/{patientId}/analyze")
    @Operation(
        summary = "Analyze patient latest spirometry session",
        description = "Evaluates the patient's most recent spirometric reading against ATS/ERS demographic reference standards, computing FEV1% predicted, FVC% predicted, FEV1/FVC ratio, ventilatory defect classification, GOLD COPD stage, and longitudinal decline rate"
    )
    public ResponseEntity<LungFunctionAnalysisResponse> analyzeLatest(@PathVariable Long patientId) {
        return ResponseEntity.ok(lungFunctionService.analyzeLatest(patientId));
    }

    @GetMapping("/patient/{patientId}/reading/{readingId}/analyze")
    @Operation(
        summary = "Analyze specific spirometry reading",
        description = "Performs complete ATS/ERS ventilatory analysis on a targeted historical spirometry test session"
    )
    public ResponseEntity<LungFunctionAnalysisResponse> analyzeReading(@PathVariable Long patientId, @PathVariable Long readingId) {
        return ResponseEntity.ok(lungFunctionService.analyzeSpecificReading(patientId, readingId));
    }

    @GetMapping("/patient/{patientId}/predicted-norms")
    @Operation(
        summary = "Calculate expected physiological reference norms",
        description = "Computes baseline predicted normal values for FEV1, FVC, PEF, and FEV1/FVC ratio tailored to the patient's chronological age, standing height, and biological sex at birth"
    )
    public ResponseEntity<LungFunctionCalculator.PredictedValues> getPredictedNorms(@PathVariable Long patientId) {
        return ResponseEntity.ok(lungFunctionService.getPredictedNormsForPatient(patientId));
    }
}
