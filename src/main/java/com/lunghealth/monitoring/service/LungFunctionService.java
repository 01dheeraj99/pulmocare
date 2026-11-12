package com.lunghealth.monitoring.service;

import com.lunghealth.monitoring.dto.response.LungFunctionAnalysisResponse;
import com.lunghealth.monitoring.exception.ResourceNotFoundException;
import com.lunghealth.monitoring.model.entity.Patient;
import com.lunghealth.monitoring.model.entity.RespiratoryData;
import com.lunghealth.monitoring.repository.RespiratoryDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LungFunctionService {

    private final PatientService patientService;
    private final RespiratoryDataRepository respiratoryDataRepository;
    private final LungFunctionCalculator calculator;

    public LungFunctionService(PatientService patientService,
                               RespiratoryDataRepository respiratoryDataRepository,
                               LungFunctionCalculator calculator) {
        this.patientService = patientService;
        this.respiratoryDataRepository = respiratoryDataRepository;
        this.calculator = calculator;
    }

    public LungFunctionAnalysisResponse analyzeLatest(Long patientId) {
        Patient patient = patientService.findEntityById(patientId);
        List<RespiratoryData> history = respiratoryDataRepository.findByPatientIdOrderByRecordedAtDesc(patientId);

        if (history.isEmpty()) {
            throw new ResourceNotFoundException("No respiratory spirometry data found for patient ID: " + patientId);
        }

        RespiratoryData latest = history.get(0);

        RespiratoryData baseline = history.size() > 1 ? history.get(history.size() - 1) : latest;

        return calculator.analyze(patient, latest, baseline);
    }

    public LungFunctionAnalysisResponse analyzeSpecificReading(Long patientId, Long readingId) {
        Patient patient = patientService.findEntityById(patientId);
        RespiratoryData reading = respiratoryDataRepository.findById(readingId)
                .orElseThrow(() -> new ResourceNotFoundException("Respiratory reading not found with ID: " + readingId));

        List<RespiratoryData> history = respiratoryDataRepository.findByPatientIdOrderByRecordedAtDesc(patientId);
        RespiratoryData baseline = history.size() > 1 ? history.get(history.size() - 1) : reading;

        return calculator.analyze(patient, reading, baseline);
    }

    public LungFunctionCalculator.PredictedValues getPredictedNormsForPatient(Long patientId) {
        Patient patient = patientService.findEntityById(patientId);
        return calculator.calculatePredictedValues(patient);
    }
}
