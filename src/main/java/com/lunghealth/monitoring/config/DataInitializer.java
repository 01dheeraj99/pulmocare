package com.lunghealth.monitoring.config;

import com.lunghealth.monitoring.dto.request.PatientRequest;
import com.lunghealth.monitoring.dto.request.RespiratoryDataRequest;
import com.lunghealth.monitoring.dto.request.SymptomLogRequest;
import com.lunghealth.monitoring.model.enums.*;
import com.lunghealth.monitoring.repository.PatientRepository;
import com.lunghealth.monitoring.service.PatientService;
import com.lunghealth.monitoring.service.RespiratoryDataService;
import com.lunghealth.monitoring.service.SymptomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Seeds the database with clinically authentic patient charts on first startup.
 * 
 * Case studies represent typical pulmonary clinic presentations:
 * 1. Chronic COPD patient with heavy smoking history & morning symptom flares
 * 2. Adult-onset asthmatic with cold-air sensitivity & nocturnal cough
 * 3. Urgent COPD/Bronchiectasis exacerbation with hemoptysis and acute distress
 * 4. Healthy non-smoking athlete establishing normal baseline spirometry
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final RespiratoryDataService respiratoryDataService;
    private final SymptomService symptomService;

    public DataInitializer(PatientRepository patientRepository,
                           PatientService patientService,
                           RespiratoryDataService respiratoryDataService,
                           SymptomService symptomService) {
        this.patientRepository = patientRepository;
        this.patientService = patientService;
        this.respiratoryDataService = respiratoryDataService;
        this.symptomService = symptomService;
    }

    @Override
    public void run(String... args) {
        if (patientRepository.count() > 0) {
            log.info("Clinical patient records already present in database; skipping seed data initialization.");
            return;
        }

        log.info("Bootstrapping clinic roster with authentic pulmonary patient case studies...");

        // -----------------------------------------------------------------------------------
        // CASE 1: Arthur Pendelton - 68yo retired boiler technician with moderate-severe COPD
        // -----------------------------------------------------------------------------------
        PatientRequest p1 = new PatientRequest();
        p1.setMedicalRecordNumber("MRN-2026-001");
        p1.setFullName("Arthur Pendelton");
        p1.setDateOfBirth(LocalDate.of(1958, 4, 12));
        p1.setSex(BiologicalSex.MALE);
        p1.setHeightCm(175.0);
        p1.setWeightKg(78.0);
        p1.setSmokingStatus(SmokingStatus.CURRENT_SMOKER);
        p1.setPackYears(42.0);
        p1.setPrimaryDiagnosis("COPD (GOLD 2-3 Phenotype)");
        p1.setOccupationalExposure("Industrial boiler maintenance, coal ash & refractory ceramic fiber (34 yrs)");
        p1.setEmergencyContactName("Martha Pendelton (Spouse)");
        p1.setEmergencyContactPhone("+1 (555) 234-8901");
        var resP1 = patientService.registerPatient(p1);

        // Baseline reading from clinic visit 2 weeks ago
        RespiratoryDataRequest r1History = new RespiratoryDataRequest();
        r1History.setPatientId(resP1.getId());
        r1History.setRecordedAt(LocalDateTime.now().minusDays(14));
        r1History.setFev1(2.05);
        r1History.setFvc(3.30);
        r1History.setPef(380.0);
        r1History.setSpo2(93.0);
        r1History.setRespiratoryRate(20);
        r1History.setHeartRate(78);
        r1History.setAqiIndex(65);
        r1History.setMeasurementContext("ROUTINE");
        r1History.setNotes("Baseline spirometry post-albuterol. Patient reports chronic morning cough.");
        respiratoryDataService.recordData(r1History);

        // Recent symptom flare recorded today
        RespiratoryDataRequest r1Current = new RespiratoryDataRequest();
        r1Current.setPatientId(resP1.getId());
        r1Current.setRecordedAt(LocalDateTime.now().minusHours(3));
        r1Current.setFev1(1.78);
        r1Current.setFvc(3.15);
        r1Current.setPef(330.0);
        r1Current.setSpo2(91.0);
        r1Current.setRespiratoryRate(24);
        r1Current.setHeartRate(88);
        r1Current.setAqiIndex(120);
        r1Current.setMeasurementContext("SYMPTOM_FLARE");
        r1Current.setNotes("Noted increased morning phlegm and breathlessness climbing home stairs. Rescue inhaler used.");
        respiratoryDataService.recordData(r1Current);

        SymptomLogRequest s1 = new SymptomLogRequest();
        s1.setPatientId(resP1.getId());
        s1.setCoughSeverity(SymptomSeverity.MODERATE);
        s1.setCoughType(CoughType.PRODUCTIVE);
        s1.setShortnessOfBreathScale(2); // mMRC grade 2: walks slower than contemporaries
        s1.setWheezingPresent(true);
        s1.setChestTightness(SymptomSeverity.MODERATE);
        s1.setSputumColor(SputumColor.YELLOW);
        s1.setNocturnalAwakening(true);
        s1.setActivityLimitation(true);
        s1.setAdditionalNotes("Yellow sputum noted for past 72 hrs. Needs frequent rest pauses during gardening.");
        symptomService.logSymptoms(s1);

        // -----------------------------------------------------------------------------------
        // CASE 2: Elena Rostova - 29yo software engineer with cold-induced cough-variant asthma
        // -----------------------------------------------------------------------------------
        PatientRequest p2 = new PatientRequest();
        p2.setMedicalRecordNumber("MRN-2026-002");
        p2.setFullName("Elena Rostova");
        p2.setDateOfBirth(LocalDate.of(1997, 8, 23));
        p2.setSex(BiologicalSex.FEMALE);
        p2.setHeightCm(168.0);
        p2.setWeightKg(61.0);
        p2.setSmokingStatus(SmokingStatus.NEVER_SMOKER);
        p2.setPackYears(0.0);
        p2.setPrimaryDiagnosis("Moderate Persistent Asthma");
        p2.setOccupationalExposure("None (Indoor tech office environment)");
        p2.setEmergencyContactName("Dmitri Rostov (Brother)");
        p2.setEmergencyContactPhone("+1 (555) 789-1123");
        var resP2 = patientService.registerPatient(p2);

        RespiratoryDataRequest r2 = new RespiratoryDataRequest();
        r2.setPatientId(resP2.getId());
        r2.setFev1(2.20);
        r2.setFvc(3.35);
        r2.setPef(320.0);
        r2.setSpo2(96.0);
        r2.setRespiratoryRate(18);
        r2.setHeartRate(74);
        r2.setAqiIndex(45);
        r2.setInhalerUsedPreTest(true);
        r2.setMeasurementContext("POST_BRONCHODILATOR");
        r2.setNotes("Post-inhaler check. Reversibility noted compared to pre-bronchodilator screening.");
        respiratoryDataService.recordData(r2);

        SymptomLogRequest s2 = new SymptomLogRequest();
        s2.setPatientId(resP2.getId());
        s2.setCoughSeverity(SymptomSeverity.MILD);
        s2.setCoughType(CoughType.DRY);
        s2.setShortnessOfBreathScale(1); // mMRC grade 1: breathless hurrying on level ground
        s2.setWheezingPresent(true);
        s2.setChestTightness(SymptomSeverity.MILD);
        s2.setSputumColor(SputumColor.NONE);
        s2.setNocturnalAwakening(true);
        s2.setActivityLimitation(false);
        s2.setAdditionalNotes("Night-time dry hacking cough flared after jogging in 4°C morning air.");
        symptomService.logSymptoms(s2);

        // -----------------------------------------------------------------------------------
        // CASE 3: Marcus Vance - 62yo quarry heavy-equipment operator in acute exacerbation
        // -----------------------------------------------------------------------------------
        PatientRequest p3 = new PatientRequest();
        p3.setMedicalRecordNumber("MRN-2026-003");
        p3.setFullName("Marcus Vance");
        p3.setDateOfBirth(LocalDate.of(1964, 11, 5));
        p3.setSex(BiologicalSex.MALE);
        p3.setHeightCm(181.0);
        p3.setWeightKg(85.0);
        p3.setSmokingStatus(SmokingStatus.CURRENT_SMOKER);
        p3.setPackYears(35.0);
        p3.setPrimaryDiagnosis("COPD with Chronic Bronchitis");
        p3.setOccupationalExposure("Silica sand, granite cutting dust & quarry blasting (22 yrs)");
        p3.setEmergencyContactName("Rachel Vance (Daughter)");
        p3.setEmergencyContactPhone("+1 (555) 911-0422");
        var resP3 = patientService.registerPatient(p3);

        RespiratoryDataRequest r3 = new RespiratoryDataRequest();
        r3.setPatientId(resP3.getId());
        r3.setFev1(1.15);
        r3.setFvc(2.45);
        r3.setPef(210.0);
        r3.setSpo2(86.0); // Severe hypoxemia
        r3.setRespiratoryRate(30); // Severe tachypnea
        r3.setHeartRate(112);
        r3.setBodyTemperatureCelsius(38.4);
        r3.setAqiIndex(168);
        r3.setMeasurementContext("EXACERBATION_EVENT");
        r3.setNotes("Urgent triage check. Patient presents with audible expiratory wheeze, fever, and blood-streaked sputum.");
        respiratoryDataService.recordData(r3);

        SymptomLogRequest s3 = new SymptomLogRequest();
        s3.setPatientId(resP3.getId());
        s3.setCoughSeverity(SymptomSeverity.SEVERE);
        s3.setCoughType(CoughType.HEMOPTYSIS); // Blood in cough
        s3.setShortnessOfBreathScale(4); // mMRC grade 4: too breathless to dress or leave home
        s3.setWheezingPresent(true);
        s3.setChestTightness(SymptomSeverity.SEVERE);
        s3.setSputumColor(SputumColor.BLOOD_STAINED);
        s3.setNocturnalAwakening(true);
        s3.setActivityLimitation(true);
        s3.setFeverPresent(true);
        s3.setAdditionalNotes("Severe orthopnea, speaks in broken short sentences. Blood streaks in thick green sputum.");
        symptomService.logSymptoms(s3);

        // -----------------------------------------------------------------------------------
        // CASE 4: Sophia Chen - 25yo marathon runner establishing healthy baseline
        // -----------------------------------------------------------------------------------
        PatientRequest p4 = new PatientRequest();
        p4.setMedicalRecordNumber("MRN-2026-004");
        p4.setFullName("Sophia Chen");
        p4.setDateOfBirth(LocalDate.of(2001, 3, 15));
        p4.setSex(BiologicalSex.FEMALE);
        p4.setHeightCm(166.0);
        p4.setWeightKg(57.0);
        p4.setSmokingStatus(SmokingStatus.NEVER_SMOKER);
        p4.setPackYears(0.0);
        p4.setPrimaryDiagnosis("None (Healthy Athletic Baseline)");
        p4.setOccupationalExposure("None (Biomedical research laboratory)");
        p4.setEmergencyContactName("Kevin Chen (Father)");
        p4.setEmergencyContactPhone("+1 (555) 441-9082");
        var resP4 = patientService.registerPatient(p4);

        RespiratoryDataRequest r4 = new RespiratoryDataRequest();
        r4.setPatientId(resP4.getId());
        r4.setFev1(3.25);
        r4.setFvc(3.85);
        r4.setPef(470.0);
        r4.setSpo2(99.0);
        r4.setRespiratoryRate(14);
        r4.setHeartRate(62);
        r4.setBodyTemperatureCelsius(36.6);
        r4.setAqiIndex(28);
        r4.setMeasurementContext("ROUTINE");
        r4.setNotes("Routine screening prior to high-altitude half-marathon training. Peak flow well above mean.");
        respiratoryDataService.recordData(r4);

        SymptomLogRequest s4 = new SymptomLogRequest();
        s4.setPatientId(resP4.getId());
        s4.setCoughSeverity(SymptomSeverity.NONE);
        s4.setCoughType(CoughType.NONE);
        s4.setShortnessOfBreathScale(0);
        s4.setWheezingPresent(false);
        s4.setChestTightness(SymptomSeverity.NONE);
        s4.setSputumColor(SputumColor.NONE);
        s4.setNocturnalAwakening(false);
        s4.setActivityLimitation(false);
        s4.setFeverPresent(false);
        s4.setAdditionalNotes("Asymptomatic runner. No respiratory complaints or exercise-induced bronchospasm.");
        symptomService.logSymptoms(s4);

        log.info("Initialized 4 pulmonary case studies with clinical vitals, spirometry curves, and symptom charts.");
    }
}
