package com.lunghealth.monitoring;

import com.lunghealth.monitoring.dto.response.LungFunctionAnalysisResponse;
import com.lunghealth.monitoring.model.entity.Patient;
import com.lunghealth.monitoring.model.entity.RespiratoryData;
import com.lunghealth.monitoring.model.enums.BiologicalSex;
import com.lunghealth.monitoring.model.enums.GoldStage;
import com.lunghealth.monitoring.model.enums.ObstructionPattern;
import com.lunghealth.monitoring.model.enums.SmokingStatus;
import com.lunghealth.monitoring.service.LungFunctionCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LungFunctionCalculatorTest {

    private LungFunctionCalculator calculator;
    private Patient malePatient;
    private Patient femalePatient;

    @BeforeEach
    void setUp() {
        calculator = new LungFunctionCalculator();

        malePatient = new Patient();
        malePatient.setId(1L);
        malePatient.setFullName("John Doe");
        malePatient.setSex(BiologicalSex.MALE);
        malePatient.setDateOfBirth(LocalDate.now().minusYears(45));
        malePatient.setHeightCm(175.0);
        malePatient.setWeightKg(75.0);
        malePatient.setSmokingStatus(SmokingStatus.NEVER_SMOKER);

        femalePatient = new Patient();
        femalePatient.setId(2L);
        femalePatient.setFullName("Jane Smith");
        femalePatient.setSex(BiologicalSex.FEMALE);
        femalePatient.setDateOfBirth(LocalDate.now().minusYears(35));
        femalePatient.setHeightCm(165.0);
        femalePatient.setWeightKg(60.0);
        femalePatient.setSmokingStatus(SmokingStatus.NEVER_SMOKER);
    }

    @Test
    @DisplayName("Should calculate valid ATS/ERS predicted values for male and female patients")
    void testCalculatePredictedValues() {
        var malePred = calculator.calculatePredictedValues(malePatient);
        assertTrue(malePred.fev1() > 2.5 && malePred.fev1() < 4.5, "Male predicted FEV1 should be physiologically normal");
        assertTrue(malePred.fvc() > 3.0 && malePred.fvc() < 5.5, "Male predicted FVC should be physiologically normal");
        assertTrue(malePred.fev1FvcRatio() >= 0.70 && malePred.fev1FvcRatio() <= 0.85);

        var femalePred = calculator.calculatePredictedValues(femalePatient);
        assertTrue(femalePred.fev1() > 2.0 && femalePred.fev1() < 4.0);
        assertTrue(femalePred.fvc() > 2.5 && femalePred.fvc() < 5.0);
    }

    @Test
    @DisplayName("Should identify Obstructive defect and GOLD 2 staging when FEV1/FVC is low")
    void testObstructivePatternAndGoldStaging() {
        var pred = calculator.calculatePredictedValues(malePatient);

        RespiratoryData reading = new RespiratoryData();
        reading.setId(10L);

        reading.setFev1(pred.fev1() * 0.65);
        reading.setFvc(pred.fvc() * 0.95);
        reading.setFev1FvcRatio(reading.getFev1() / reading.getFvc());
        reading.setPef(pred.pef() * 0.70);

        LungFunctionAnalysisResponse analysis = calculator.analyze(malePatient, reading, reading);

        assertEquals(ObstructionPattern.OBSTRUCTIVE, analysis.getObstructionPattern());
        assertEquals(GoldStage.GOLD_2_MODERATE, analysis.getGoldStage());
        assertTrue(analysis.getFev1FvcRatioMeasured() < 0.70);
    }

    @Test
    @DisplayName("Should identify Normal spirometry when ratio and FVC are both preserved")
    void testNormalPattern() {
        var pred = calculator.calculatePredictedValues(femalePatient);

        RespiratoryData reading = new RespiratoryData();
        reading.setId(11L);
        reading.setFev1(pred.fev1() * 0.95);
        reading.setFvc(pred.fvc() * 0.98);
        reading.setFev1FvcRatio(reading.getFev1() / reading.getFvc());
        reading.setPef(pred.pef());

        LungFunctionAnalysisResponse analysis = calculator.analyze(femalePatient, reading, reading);

        assertEquals(ObstructionPattern.NORMAL, analysis.getObstructionPattern());
        assertEquals(GoldStage.NOT_APPLICABLE, analysis.getGoldStage());
        assertTrue(analysis.getFev1PercentPredicted() >= 80.0);
    }

    @Test
    @DisplayName("Should detect rapid decline when current FEV1 drops by more than 12% from baseline")
    void testLongitudinalDeclineDetection() {
        var pred = calculator.calculatePredictedValues(malePatient);

        RespiratoryData baseline = new RespiratoryData();
        baseline.setId(1L);
        baseline.setFev1(3.0);
        baseline.setFvc(4.0);

        RespiratoryData current = new RespiratoryData();
        current.setId(2L);
        current.setFev1(2.4);
        current.setFvc(3.6);

        LungFunctionAnalysisResponse analysis = calculator.analyze(malePatient, current, baseline);

        assertEquals("RAPID_DECLINE", analysis.getTrendDirection());
        assertTrue(analysis.getFev1ChangeFromBaselinePercent() < -12.0);
    }
}
