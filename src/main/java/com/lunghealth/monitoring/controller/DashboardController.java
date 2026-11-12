package com.lunghealth.monitoring.controller;

import com.lunghealth.monitoring.dto.response.DashboardMetricsResponse;
import com.lunghealth.monitoring.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller serving population-level respiratory health surveillance and clinical triage metrics.
 *
 * <p>Provides aggregated intelligence for clinical directors and nursing supervisors, tracking overall
 * registry volume, triage distribution across risk tiers (Low, Moderate, High, Critical), active emergency
 * alerts requiring immediate intervention, and cohort mean oxygenation values.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@RestController
@RequestMapping("/api/dashboard")
@Tag(name = "Clinical Population Surveillance", description = "Cohort Analytics, Risk Stratification Triage & System Telemetry — Metropolitan Pulmonary Associates")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/metrics")
    @Operation(
        summary = "Retrieve population health surveillance metrics",
        description = "Returns real-time aggregated clinical metrics: total active patients, total telemetry maneuvers collected, unacknowledged emergency alerts, population mean SpO2, and risk category breakdown"
    )
    public ResponseEntity<DashboardMetricsResponse> getMetrics() {
        return ResponseEntity.ok(dashboardService.getMetrics());
    }
}
