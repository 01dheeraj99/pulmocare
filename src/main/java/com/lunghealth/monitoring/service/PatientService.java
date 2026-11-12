package com.lunghealth.monitoring.service;

import com.lunghealth.monitoring.dto.request.PatientRequest;
import com.lunghealth.monitoring.dto.response.PatientResponse;
import com.lunghealth.monitoring.exception.InvalidClinicalDataException;
import com.lunghealth.monitoring.exception.ResourceNotFoundException;
import com.lunghealth.monitoring.model.entity.Patient;
import com.lunghealth.monitoring.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    public PatientResponse registerPatient(PatientRequest request) {
        if (patientRepository.existsByMedicalRecordNumber(request.getMedicalRecordNumber())) {
            throw new InvalidClinicalDataException("Medical Record Number already registered: " + request.getMedicalRecordNumber());
        }

        Patient patient = new Patient();
        copyProperties(request, patient);
        Patient saved = patientRepository.save(patient);
        return PatientResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientById(Long id) {
        Patient patient = findEntityById(id);
        return PatientResponse.fromEntity(patient);
    }

    @Transactional(readOnly = true)
    public PatientResponse getPatientByMrn(String mrn) {
        Patient patient = patientRepository.findByMedicalRecordNumber(mrn)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with MRN: " + mrn));
        return PatientResponse.fromEntity(patient);
    }

    @Transactional(readOnly = true)
    public List<PatientResponse> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(PatientResponse::fromEntity)
                .toList();
    }

    public PatientResponse updatePatient(Long id, PatientRequest request) {
        Patient patient = findEntityById(id);

        if (!patient.getMedicalRecordNumber().equals(request.getMedicalRecordNumber()) &&
                patientRepository.existsByMedicalRecordNumber(request.getMedicalRecordNumber())) {
            throw new InvalidClinicalDataException("Medical Record Number already in use by another patient: " + request.getMedicalRecordNumber());
        }

        copyProperties(request, patient);
        Patient updated = patientRepository.save(patient);
        return PatientResponse.fromEntity(updated);
    }

    public void deletePatient(Long id) {
        Patient patient = findEntityById(id);
        patientRepository.delete(patient);
    }

    @Transactional(readOnly = true)
    public Patient findEntityById(Long id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + id));
    }

    private void copyProperties(PatientRequest req, Patient p) {
        p.setMedicalRecordNumber(req.getMedicalRecordNumber());
        p.setFullName(req.getFullName());
        p.setDateOfBirth(req.getDateOfBirth());
        p.setSex(req.getSex());
        p.setHeightCm(req.getHeightCm());
        p.setWeightKg(req.getWeightKg());
        p.setSmokingStatus(req.getSmokingStatus());
        p.setPackYears(req.getPackYears() != null ? req.getPackYears() : 0.0);
        p.setPrimaryDiagnosis(req.getPrimaryDiagnosis());
        p.setOccupationalExposure(req.getOccupationalExposure());
        p.setEmergencyContactName(req.getEmergencyContactName());
        p.setEmergencyContactPhone(req.getEmergencyContactPhone());
    }
}
