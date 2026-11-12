package com.lunghealth.monitoring.controller;

import com.lunghealth.monitoring.dto.request.RiskEvaluationRequest;
import com.lunghealth.monitoring.dto.response.RiskAssessmentResponse;
import com.lunghealth.monitoring.service.RiskAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for multi-factorial pulmonary risk stratification and clinical decision support.
 *
 * <p>Integrates physiological measurements, spirometry curves, symptom severity scores, and tobacco/occupational
 * exposure indices into a normalized 0-100 composite risk score. Classifies patient status into LOW, MODERATE,
 * HIGH, or CRITICAL tiers and generates tailored, evidence-based recommendations for attending pulmonologists.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@RestController
@RequestMapping("/api/risk-assessments")
@Tag(name = "Clinical Risk Stratification", description = "Multi-Factor Pulmonary Risk Scoring & Clinical Decision Support — Metropolitan Pulmonary Associates")
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    public RiskAssessmentController(RiskAssessmentService riskAssessmentService) {
        this.riskAssessmentService = riskAssessmentService;
    }

    @PostMapping("/evaluate")
    @Operation(
        summary = "Execute multi-factorial risk evaluation",
        description = "Performs on-demand algorithmic risk synthesis combining vital signs, spirometry, symptom burden, and patient background, automatically generating clinical recommendations and triggering alerts if critical safety thresholds are breached"
    )
    public ResponseEntity<RiskAssessmentResponse> evaluateRisk(@Valid @RequestBody RiskEvaluationRequest request) {
        return ResponseEntity.ok(riskAssessmentService.evaluateRisk(request));
    }

    @GetMapping("/patient/{patientId}/latest")
    @Operation(
        summary = "Retrieve latest risk assessment",
        description = "Fetches the most current risk stratification score, identified risk factors, GOLD stage, GINA control level, and actionable physician guidance notes for the specified patient"
    )
    public ResponseEntity<RiskAssessmentResponse> getLatestAssessment(@PathVariable Long patientId) {
        RiskAssessmentResponse response = riskAssessmentService.getLatestAssessment(patientId);
        return response != null ? ResponseEntity.ok(response) : ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}/history")
    @Operation(
        summary = "Retrieve patient risk trajectory history",
        description = "Returns chronologically ordered risk assessments to track clinical response to pulmonary rehabilitation, inhaler escalations, or steroid therapy"
    )
    public ResponseEntity<List<RiskAssessmentResponse>> getAssessmentHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(riskAssessmentService.getAssessmentHistory(patientId));
    }
}
