package com.lunghealth.monitoring.service;

import com.lunghealth.monitoring.dto.request.RespiratoryDataRequest;
import com.lunghealth.monitoring.dto.response.RespiratoryDataResponse;
import com.lunghealth.monitoring.model.entity.Patient;
import com.lunghealth.monitoring.model.entity.RespiratoryData;
import com.lunghealth.monitoring.repository.RespiratoryDataRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class RespiratoryDataService {

    private final RespiratoryDataRepository respiratoryDataRepository;
    private final PatientService patientService;
    private final LungFunctionCalculator lungFunctionCalculator;
    private final RiskAssessmentService riskAssessmentService;

    public RespiratoryDataService(RespiratoryDataRepository respiratoryDataRepository,
                                  PatientService patientService,
                                  LungFunctionCalculator lungFunctionCalculator,
                                  @Lazy RiskAssessmentService riskAssessmentService) {
        this.respiratoryDataRepository = respiratoryDataRepository;
        this.patientService = patientService;
        this.lungFunctionCalculator = lungFunctionCalculator;
        this.riskAssessmentService = riskAssessmentService;
    }

    public RespiratoryDataResponse recordData(RespiratoryDataRequest request) {
        Patient patient = patientService.findEntityById(request.getPatientId());

        RespiratoryData data = new RespiratoryData();
        data.setPatient(patient);
        data.setRecordedAt(request.getRecordedAt() != null ? request.getRecordedAt() : LocalDateTime.now());
        data.setFev1(request.getFev1());
        data.setFvc(request.getFvc());
        data.setFev1FvcRatio(round(request.getFev1() / request.getFvc(), 3));
        data.setPef(request.getPef());
        data.setSpo2(request.getSpo2());
        data.setRespiratoryRate(request.getRespiratoryRate());
        data.setHeartRate(request.getHeartRate());
        data.setBodyTemperatureCelsius(request.getBodyTemperatureCelsius());
        data.setAqiIndex(request.getAqiIndex());
        data.setInhalerUsedPreTest(request.getInhalerUsedPreTest() != null ? request.getInhalerUsedPreTest() : false);
        data.setMeasurementContext(request.getMeasurementContext());
        data.setNotes(request.getNotes());

        LungFunctionCalculator.PredictedValues pred = lungFunctionCalculator.calculatePredictedValues(patient);
        if (pred.fev1() > 0) {
            data.setFev1PredictedPercent(round((request.getFev1() / pred.fev1()) * 100.0, 1));
        }
        if (pred.fvc() > 0) {
            data.setFvcPredictedPercent(round((request.getFvc() / pred.fvc()) * 100.0, 1));
        }
        if (pred.pef() > 0 && request.getPef() != null) {
            data.setPefPredictedPercent(round((request.getPef() / pred.pef()) * 100.0, 1));
        }

        RespiratoryData saved = respiratoryDataRepository.save(data);

        try {
            riskAssessmentService.evaluateRiskForPatient(patient.getId(), saved.getId(), null);
        } catch (Exception e) {

        }

        return RespiratoryDataResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<RespiratoryDataResponse> getPatientHistory(Long patientId) {

        patientService.findEntityById(patientId);
        return respiratoryDataRepository.findByPatientIdOrderByRecordedAtDesc(patientId)
                .stream()
                .map(RespiratoryDataResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RespiratoryData getLatestEntity(Long patientId) {
        return respiratoryDataRepository.findFirstByPatientIdOrderByRecordedAtDesc(patientId).orElse(null);
    }

    @Transactional(readOnly = true)
    public RespiratoryData findEntityById(Long id) {
        return respiratoryDataRepository.findById(id).orElse(null);
    }

    private double round(double val, int scale) {
        return BigDecimal.valueOf(val).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }
}
