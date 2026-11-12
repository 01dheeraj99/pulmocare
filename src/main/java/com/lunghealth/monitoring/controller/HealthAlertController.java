package com.lunghealth.monitoring.controller;

import com.lunghealth.monitoring.dto.response.HealthAlertResponse;
import com.lunghealth.monitoring.service.RiskAssessmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for emergency safety alerts, clinician triage queues, and alert acknowledgment workflows.
 *
 * <p>Enables respiratory therapists, triage nurses, and on-call pulmonologists to review real-time
 * notifications triggered by life-threatening physiologic events (critical hypoxemia SpO2 &lt; 85%, tachypneic
 * crisis RR &gt; 28 bpm, hemoptysis, or acute spirometric loss) and maintain documented audit trails of clinical review.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@RestController
@RequestMapping("/api/alerts")
@Tag(name = "Emergency Clinical Alerts", description = "Real-Time Respiratory Alert Triage & Clinician Acknowledgment — Metropolitan Pulmonary Associates")
public class HealthAlertController {

    private final RiskAssessmentService riskAssessmentService;

    public HealthAlertController(RiskAssessmentService riskAssessmentService) {
        this.riskAssessmentService = riskAssessmentService;
    }

    @GetMapping("/active")
    @Operation(
        summary = "Retrieve active, unacknowledged emergency alerts",
        description = "Returns all high-priority and critical safety alerts across the monitored patient population awaiting clinician review and intervention"
    )
    public ResponseEntity<List<HealthAlertResponse>> getActiveAlerts() {
        return ResponseEntity.ok(riskAssessmentService.getActiveAlerts());
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Retrieve alert history for a patient",
        description = "Returns complete chronological safety alert logs for the specified patient, including historical resolution timestamps"
    )
    public ResponseEntity<List<HealthAlertResponse>> getPatientAlerts(@PathVariable Long patientId) {
        return ResponseEntity.ok(riskAssessmentService.getPatientAlerts(patientId));
    }

    @PutMapping("/{alertId}/acknowledge")
    @Operation(
        summary = "Acknowledge clinical alert",
        description = "Records clinician acknowledgment and review timestamp, moving the safety alert from active triage to documented audit status"
    )
    public ResponseEntity<HealthAlertResponse> acknowledgeAlert(@PathVariable Long alertId) {
        return ResponseEntity.ok(riskAssessmentService.acknowledgeAlert(alertId));
    }
}
