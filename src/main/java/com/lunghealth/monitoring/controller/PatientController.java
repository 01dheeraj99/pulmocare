package com.lunghealth.monitoring.controller;

import com.lunghealth.monitoring.dto.request.PatientRequest;
import com.lunghealth.monitoring.dto.response.PatientResponse;
import com.lunghealth.monitoring.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller managing patient registration, clinical baseline intake, and demographic records.
 *
 * <p>Enables pulmonary care coordinators and clinicians to register individuals, maintain anthropometric
 * measurements required for ATS/ERS spirometry prediction regressions (age, standing height, biological sex),
 * and record lifetime tobacco pack-years and occupational inhalation exposures.</p>
 *
 * @author Dr. Eleanor Vance, MD, FCCP / PulmoCare Clinical Informatics
 */
@RestController
@RequestMapping("/api/patients")
@Tag(name = "Patients", description = "Patient Profile & Pulmonary Baseline Registry — Metropolitan Pulmonary Associates")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    @Operation(
        summary = "Register a new pulmonary patient",
        description = "Enrolls a new patient into the monitoring registry, establishing baseline anthropometrics (height, sex, DOB) and smoking history for ATS/ERS algorithms"
    )
    public ResponseEntity<PatientResponse> registerPatient(@Valid @RequestBody PatientRequest request) {
        PatientResponse response = patientService.registerPatient(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(
        summary = "Retrieve active patient cohort",
        description = "Returns all registered pulmonary patients with computed physiological age, current BMI, and primary diagnoses"
    )
    public ResponseEntity<List<PatientResponse>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Fetch patient by internal ID",
        description = "Retrieves complete clinical profile by system database identifier"
    )
    public ResponseEntity<PatientResponse> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/mrn/{mrn}")
    @Operation(
        summary = "Locate patient by Medical Record Number (MRN)",
        description = "Queries the registry using the hospital or clinic unique Medical Record Number"
    )
    public ResponseEntity<PatientResponse> getPatientByMrn(@PathVariable String mrn) {
        return ResponseEntity.ok(patientService.getPatientByMrn(mrn));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update patient clinical profile",
        description = "Updates patient anthropometric measurements, smoking exposure pack-years, or contact information"
    )
    public ResponseEntity<PatientResponse> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientRequest request) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Deactivate/remove patient record",
        description = "Removes a patient record and cascaded respiratory telemetry from active clinical monitoring"
    )
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
}
