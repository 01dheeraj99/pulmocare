package com.lunghealth.monitoring.controller;

import com.lunghealth.monitoring.dto.request.RespiratoryDataRequest;
import com.lunghealth.monitoring.dto.response.RespiratoryDataResponse;
import com.lunghealth.monitoring.service.RespiratoryDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for ingesting spirometry curves, vital telemetry, and environmental exposure metrics.
 *
 * <p>Serves as the high-fidelity telemetry ingest gateway for portable spirometers, Bluetooth pulse oximeters,
 * and respiratory therapy observation workflows. Automatically calculates ATS/ERS percentage-predicted values
 * (FEV1%, FVC%, PEF%) and derived Tiffeneau-Pinelli indices upon ingestion.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@RestController
@RequestMapping("/api/respiratory-data")
@Tag(name = "Respiratory Telemetry", description = "Spirometry & Vital Signs Telemetry Ingestion — Metropolitan Pulmonary Associates")
public class RespiratoryDataController {

    private final RespiratoryDataService respiratoryDataService;

    public RespiratoryDataController(RespiratoryDataService respiratoryDataService) {
        this.respiratoryDataService = respiratoryDataService;
    }

    @PostMapping
    @Operation(
        summary = "Ingest spirometry and respiratory vitals",
        description = "Collects forced expiratory spirometry (FEV1, FVC, PEF) and vital signs (SpO2, Respiratory Rate, Heart Rate, Temperature, AQI) and automatically computes physiological percentage-predicted values against demographic norms"
    )
    public ResponseEntity<RespiratoryDataResponse> recordRespiratoryData(@Valid @RequestBody RespiratoryDataRequest request) {
        RespiratoryDataResponse response = respiratoryDataService.recordData(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/patient/{patientId}")
    @Operation(
        summary = "Retrieve patient respiratory telemetry history",
        description = "Returns chronological physiological measurements and spirometry session history for longitudinal trajectory modeling"
    )
    public ResponseEntity<List<RespiratoryDataResponse>> getPatientHistory(@PathVariable Long patientId) {
        return ResponseEntity.ok(respiratoryDataService.getPatientHistory(patientId));
    }
}
