package com.lunghealth.monitoring.service;

import com.lunghealth.monitoring.dto.request.SymptomLogRequest;
import com.lunghealth.monitoring.dto.response.SymptomLogResponse;
import com.lunghealth.monitoring.model.entity.Patient;
import com.lunghealth.monitoring.model.entity.SymptomLog;
import com.lunghealth.monitoring.repository.SymptomLogRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SymptomService {

    private final SymptomLogRepository symptomLogRepository;
    private final PatientService patientService;
    private final RiskAssessmentService riskAssessmentService;

    public SymptomService(SymptomLogRepository symptomLogRepository,
                          PatientService patientService,
                          @Lazy RiskAssessmentService riskAssessmentService) {
        this.symptomLogRepository = symptomLogRepository;
        this.patientService = patientService;
        this.riskAssessmentService = riskAssessmentService;
    }

    public SymptomLogResponse logSymptoms(SymptomLogRequest request) {
        Patient patient = patientService.findEntityById(request.getPatientId());

        SymptomLog log = new SymptomLog();
        log.setPatient(patient);
        log.setRecordedAt(request.getRecordedAt() != null ? request.getRecordedAt() : LocalDateTime.now());
        log.setCoughSeverity(request.getCoughSeverity());
        log.setCoughType(request.getCoughType());
        log.setShortnessOfBreathScale(request.getShortnessOfBreathScale());
        log.setWheezingPresent(request.getWheezingPresent() != null ? request.getWheezingPresent() : false);
        log.setChestTightness(request.getChestTightness());
        log.setSputumColor(request.getSputumColor());
        log.setNocturnalAwakening(request.getNocturnalAwakening() != null ? request.getNocturnalAwakening() : false);
        log.setActivityLimitation(request.getActivityLimitation() != null ? request.getActivityLimitation() : false);
        log.setFeverPresent(request.getFeverPresent() != null ? request.getFeverPresent() : false);
        log.setAdditionalNotes(request.getAdditionalNotes());

        log.calculateScore();
        SymptomLog saved = symptomLogRepository.save(log);

        try {
            riskAssessmentService.evaluateRiskForPatient(patient.getId(), null, saved.getId());
        } catch (Exception e) {

        }

        return SymptomLogResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<SymptomLogResponse> getPatientSymptoms(Long patientId) {
        patientService.findEntityById(patientId);
        return symptomLogRepository.findByPatientIdOrderByRecordedAtDesc(patientId)
                .stream()
                .map(SymptomLogResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public SymptomLog getLatestEntity(Long patientId) {
        return symptomLogRepository.findFirstByPatientIdOrderByRecordedAtDesc(patientId).orElse(null);
    }

    @Transactional(readOnly = true)
    public SymptomLog findEntityById(Long id) {
        return symptomLogRepository.findById(id).orElse(null);
    }
}
