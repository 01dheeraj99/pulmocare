# PulmoCare Clinical Suite
### Pulmonary Health & Lung Function Monitoring System
**Metropolitan Pulmonary Associates & Lung Diagnostic Center**  
*Clinical Medical Director: Dr. Eleanor Vance, MD, FCCP*  
*Diagnostic Facility CLIA ID: 36D0984122*

---

## 🩺 Clinical Overview & Mission

Chronic respiratory conditions—such as **Chronic Obstructive Pulmonary Disease (COPD)**, **Bronchial Asthma**, and **Interstitial Lung Disease (ILD)**—are characterized by insidious disease progression and sudden, life-threatening exacerbations. Traditional episodic clinic visits often detect deteriorations only after acute respiratory failure occurs.

**PulmoCare** is an enterprise-grade clinical decision support and remote patient monitoring platform designed to bridge this gap. Built on **Java 21/22** and **Spring Boot 3.3.4**, PulmoCare delivers continuous physiological monitoring, standardized ATS/ERS spirometric interpretation, multi-factor risk stratification, and emergency safety triaging to empower pulmonary care teams.

```
                           +-------------------------------------+
                           |      Pulmonary Patient Cohort       |
                           | (Home Spirometry & Pulse Oximeters) |
                           +------------------+------------------+
                                              |
                                     REST Telemetry & Symptoms
                                              |
                                              v
+------------------------------------------------------------------------------------------+
|                         PulmoCare Clinical Engine (Spring Boot 3)                        |
|                                                                                          |
|  +--------------------+  +----------------------+  +------------------+  +-------------+ |
|  | ATS/ERS Spirometry |  | mMRC Symptom Complex |  | Multi-Factor Risk|  | Safety Alert| |
|  | Regression Models  |  | Anthonisen Purulence |  | Decision Engine  |  | Triage & Log| |
|  +--------------------+  +----------------------+  +------------------+  +-------------+ |
+---------------------------------------------+--------------------------------------------+
                                              |
                    +-------------------------+-------------------------+
                    |                                                   |
                    v                                                   v
     +------------------------------+                    +------------------------------+
     |     Interactive Clinician    |                    |  Hospital-Grade Diagnostics  |
     |      Web Dashboard (SPA)     |                    |  - Certified PDF Reports     |
     | - Real-time Vital Badges     |                    |  - Interoperable JSON (FHIR) |
     | - Longitudinal Trend Curves  |                    |  - Research CSV Telemetry    |
     +------------------------------+                    +------------------------------+
```

---

## 🌟 Clinical Core Modules

### 1. Respiratory Telemetry & Demographic Normalization
- **Inbound Physiological Vitals**:
  - $SpO_2$ (Peripheral capillary oxygen saturation, $50.0\% - 100.0\%$) with hypoxemia severity grading.
  - **Respiratory Rate** (breaths per minute) calibrated against National Early Warning Score (NEWS2) thresholds for tachypnea ($> 28\text{ bpm}$) and bradypnea ($< 8\text{ bpm}$).
  - **Heart Rate** ($\text{bpm}$) and **Body Temperature** ($^\circ\text{C}$).
  - **Ambient AQI** (Air Quality Index, $0 - 500$) exposure logging to correlate pollution spikes with bronchoconstriction.
- **Spirometry Maneuvers**:
  - $FEV_1$ (Forced Expiratory Volume in 1 second, Liters).
  - $FVC$ (Forced Vital Capacity, Liters).
  - $FEV_1/FVC$ Ratio (Tiffeneau-Pinelli index, automatically derived).
  - $PEF$ (Peak Expiratory Flow, L/min).
  - Pre-test bronchodilator tracking (`inhalerUsedPreTest`) for reversibility testing.
- **Automatic % Predicted Derivation**: Uses ATS/ERS Knudson and NHANES III demographic regressions based on chronological age, standing height, and biological sex at birth.

### 2. Symptom Complex & Functional Burden Scoring
- **mMRC Dyspnea Scale (Grades 0–4)**: Standardized clinical breathlessness stratification:
  - *Grade 0*: Breathless only with strenuous exertion.
  - *Grade 1*: Short of breath when hurrying or walking up a slight hill.
  - *Grade 2*: Walks slower than contemporaries or stops for breath on level ground.
  - *Grade 3*: Stops for breath after walking $\sim 100\text{ meters}$ or a few minutes.
  - *Grade 4*: Too breathless to leave the house or breathless when dressing.
- **Anthonisen Sputum Purulence Criteria**: Tracks progression from *Clear/White* (mucoid) to *Yellow* (mucopurulent) and *Green/Rust-Brown* (purulent, indicating active bacterial infection and antibiotic indications).
- **Cough Phenotyping**: Categorizes cough into *Dry Irritative*, *Productive Bronchitic*, and red-flag *Hemoptysis*.
- **GINA Instability Markers**: Captures nocturnal awakenings, activity limitations, and acute febrile response.
- **Composite Symptom Burden Score ($0 - 30$)**: Synthesized in real time to trigger escalation protocols.

### 3. ATS/ERS Spirometric Interpretation & GOLD Staging
- **Demographic Reference Equations**:
  $$\text{Male } FVC = 0.0576 \times \text{Height (cm)} - 0.0269 \times \text{Age} - 4.34$$
  $$\text{Male } FEV_1 = 0.0430 \times \text{Height (cm)} - 0.0290 \times \text{Age} - 2.49$$
  $$\text{Female } FVC = 0.0443 \times \text{Height (cm)} - 0.0260 \times \text{Age} - 2.89$$
  $$\text{Female } FEV_1 = 0.0395 \times \text{Height (cm)} - 0.0250 \times \text{Age} - 2.60$$
- **Ventilatory Defect Classification**:
  - **Normal Spirometry**: $FEV_1/FVC \ge 0.70$ and $FVC \ge 80\%$ predicted.
  - **Obstructive Defect**: $FEV_1/FVC < 0.70$ (e.g., COPD, Asthma, Bronchiectasis).
  - **Restrictive Pattern**: $FEV_1/FVC \ge 0.70$ and $FVC < 80\%$ predicted (e.g., Pulmonary Fibrosis, Chest wall deformities).
  - **Mixed Defect**: Concomitant reduction in both ratio and vital capacity.
- **GOLD COPD Staging**:
  - `GOLD 1: Mild` ($FEV_1 \ge 80\%$ predicted)
  - `GOLD 2: Moderate` ($50\% \le FEV_1 < 80\%$ predicted)
  - `GOLD 3: Severe` ($30\% \le FEV_1 < 50\%$ predicted)
  - `GOLD 4: Very Severe` ($FEV_1 < 30\%$ predicted)
- **Longitudinal Trend Vector**: Compares current $FEV_1$ against historical baseline to flag rapid functional decline ($> 12\%$ loss).

### 4. Multi-Factor Risk Stratification & Clinical Decision Support
- **Composite Pulmonary Risk Index ($0 - 100$)**:
  - Synthesizes hypoxemia severity, tachypnea, airflow limitation, symptom burden, cumulative tobacco pack-years ($(\text{cigs/day} / 20) \times \text{years}$), and environmental AQI exposure.
  - Stratified into **LOW ($0-24$)**, **MODERATE ($25-54$)**, **HIGH ($55-79$)**, and **CRITICAL ($80-100$)**.
- **GINA Asthma Control Staging**: Staged into *Well-Controlled*, *Partly-Controlled*, or *Uncontrolled*.
- **Automated Emergency Alerts & Provider Triage**:
  - Critical Hypoxemia ($SpO_2 < 85\%$)
  - Severe Tachypneic Crisis ($RR > 28\text{ bpm}$)
  - Hemoptysis warning with urgent diagnostic directive
  - Bacterial infection with systemic fever
  - Acute airflow obstruction flare-up
- Full clinical audit trail with clinician acknowledgment timestamps.

### 5. Certified Clinical Documentation & Data Export
- **Certified PDF Diagnostic Report**: Built using **OpenPDF**, formatted with Metropolitan Pulmonary Associates letterhead, CLIA accreditation badges, demographic and anthropometric summaries, spirometric parameter tables with ATS/ERS ranges, risk tier badges, physician clinical directives, and an official signature block by Dr. Eleanor Vance, MD, FCCP.
- **Structured JSON Clinical Summary**: Designed for direct FHIR/EHR ingestion, bundling all diagnostic dimensions into a clean API response.
- **CSV Telemetry Export**: One-click download of longitudinal spirometry and vitals for clinical research and registry submissions.

### 6. Clinician Dashboard
- Interactive single-page interface served directly at `http://localhost:8080/`.
- Patient switcher chips with avatar initials, diagnosis tags, and age indicators.
- Live vital stat cards ($SpO_2$ with hypoxemia tier, Respiratory Rate, Heart Rate, Ambient AQI).
- ATS/ERS Spirometry table displaying Measured, Predicted, and % Predicted values.
- Interactive **Chart.js** longitudinal trend curves tracking $FEV_1$ and $SpO_2$ trajectories over time.
- Integrated modal dialogs for new patient intake, telemetry ingestion, and symptom logging.
- Emergency alert banner with one-click provider acknowledgment.

---

## 📋 Pre-Loaded Clinical Cases

When launched, the system initializes four realistic clinical cases reflecting diverse patient presentations:

| Patient | MRN | Profile | Primary Diagnosis | Key Clinical Finding | Risk Tier |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Arthur Pendelton** | `MRN-2026-001` | 68 yo M, 42 pack-years | COPD (GOLD 2/3) | $FEV_1/FVC = 0.569$, $FEV_1 = 57\%$ pred, yellow sputum | **MODERATE** |
| **Elena Rostova** | `MRN-2026-002` | 29 yo F, Never smoker | Cough-Variant Asthma | Nocturnal cough, wheezing, partly controlled under GINA | **MODERATE** |
| **Marcus Vance** | `MRN-2026-003` | 62 yo M, Sandblaster | Severe COPD / Exacerbation | $SpO_2 = 86\%$, $RR = 30\text{ bpm}$, Hemoptysis reported | **CRITICAL** |
| **Sophia Chen** | `MRN-2026-004` | 25 yo F, Marathon runner | Healthy Baseline | $SpO_2 = 99\%$, $FEV_1/FVC = 0.827$, $FEV_1 = 3.25\text{ L}$ ($100\%$ pred) | **LOW** |

---

## 🏗️ Technical Architecture

```
src/main/java/com/lunghealth/monitoring/
├── config/
│   ├── DataInitializer.java              # Seeds realistic clinical cohort
│   └── OpenApiConfig.java                # Swagger OpenAPI 3.0 configuration
├── controller/                           # REST API Controller layer
│   ├── DashboardController.java          # /api/dashboard
│   ├── HealthAlertController.java        # /api/alerts
│   ├── LungFunctionController.java       # /api/lung-function
│   ├── PatientController.java            # /api/patients
│   ├── ReportController.java             # /api/reports (JSON, PDF, CSV)
│   ├── RespiratoryDataController.java    # /api/respiratory-data
│   ├── RiskAssessmentController.java     # /api/risk-assessments
│   └── SymptomController.java            # /api/symptoms
├── dto/
│   ├── request/                          # Validated inbound request payloads
│   └── response/                         # Strongly-typed clinical response models
├── exception/
│   ├── ErrorResponse.java                # Standard RFC-7807 error schema
│   ├── GlobalExceptionHandler.java       # Centralized HTTP exception handler
│   ├── InvalidClinicalDataException.java
│   └── ResourceNotFoundException.java
├── model/
│   ├── entity/                           # JPA domain entities
│   │   ├── HealthAlert.java
│   │   ├── Patient.java
│   │   ├── RespiratoryData.java
│   │   ├── RiskAssessment.java
│   │   └── SymptomLog.java
│   └── enums/                            # Clinical taxonomies & classifications
├── repository/                           # Spring Data JPA repositories with JPQL
└── service/                              # Core pulmonary computation algorithms
    ├── DashboardService.java
    ├── LungFunctionCalculator.java       # ATS/ERS Knudson/NHANES spirometry engine
    ├── LungFunctionService.java
    ├── PatientService.java
    ├── ReportService.java                # OpenPDF report & CSV generator
    ├── RespiratoryDataService.java
    ├── RiskAssessmentEngine.java         # Multi-factor clinical decision engine
    ├── RiskAssessmentService.java
    └── SymptomService.java
```

---

## 🚀 Getting Started

### Prerequisites
- **Java Development Kit (JDK) 21 or 22** (`java -version`)
- **Apache Maven 3.8+** (`mvn -v`)

### 1. Execute Unit & Integration Tests
```powershell
mvn clean test
```
*Executes all 19 unit and integration tests across spirometric regression, risk algorithms, REST endpoints, and PDF generation.*

### 2. Package the Executable Fat JAR
```powershell
mvn clean package -DskipTests
```
*Builds the production-ready executable archive at `target/lung-health-monitoring-1.0.0.jar`.*

### 3. Launch the Server
```powershell
java -jar target/lung-health-monitoring-1.0.0.jar
```
*The application starts on port `8080` in approximately 5–7 seconds with H2 in-memory storage and pre-loaded clinical data.*

---

## 🌐 Clinical Portals & Endpoints

| Portal / Resource | URL | Purpose |
| :--- | :--- | :--- |
| **Interactive Clinician Dashboard** | `http://localhost:8080/` | Web interface for patient switching, telemetry charts, and clinical forms |
| **Interactive Swagger UI Docs** | `http://localhost:8080/swagger-ui.html` | OpenAPI documentation and interactive endpoint testing |
| **OpenAPI Specification (JSON)** | `http://localhost:8080/v3/api-docs` | Raw OpenAPI v3 specification for client generation |
| **H2 Database Web Console** | `http://localhost:8080/h2-console` | Inspect database tables (`jdbc:h2:mem:lungdb`, user: `sa`, password: *(empty)*) |

---

## 🩺 REST API Quick Reference

### 1. Patient Registry (`/api/patients`)
- `POST /api/patients` - Register a patient with anthropometric baseline & smoking history.
- `GET /api/patients` - List all registered patients.
- `GET /api/patients/{id}` - Retrieve patient profile by database ID.
- `GET /api/patients/mrn/{mrn}` - Locate patient by hospital Medical Record Number.
- `PUT /api/patients/{id}` - Update demographics, weight, or diagnosis.
- `DELETE /api/patients/{id}` - Remove patient record.

### 2. Respiratory Telemetry (`/api/respiratory-data`)
- `POST /api/respiratory-data` - Ingest $FEV_1, FVC, PEF, SpO_2, RR$, heart rate, temperature, and AQI.
- `GET /api/respiratory-data/patient/{patientId}` - Retrieve patient longitudinal telemetry history.

### 3. Symptom Complex (`/api/symptoms`)
- `POST /api/symptoms` - Log cough character, mMRC dyspnea score, sputum purulence, and fever.
- `GET /api/symptoms/patient/{patientId}` - Retrieve chronological symptom evaluations.

### 4. Lung Function Monitoring (`/api/lung-function`)
- `GET /api/lung-function/patient/{patientId}/analyze` - Run ATS/ERS analysis on latest spirometry reading.
- `GET /api/lung-function/patient/{patientId}/reading/{readingId}/analyze` - Analyze a specific historical maneuver.
- `GET /api/lung-function/patient/{patientId}/predicted-norms` - Compute predicted baseline norms ($FEV_1, FVC, PEF$).

### 5. Risk Assessment & Safety Alerts (`/api/risk-assessments`, `/api/alerts`)
- `POST /api/risk-assessments/evaluate` - Execute on-demand multi-factor risk evaluation.
- `GET /api/risk-assessments/patient/{patientId}/latest` - Retrieve current risk tier and recommendations.
- `GET /api/risk-assessments/patient/{patientId}/history` - Retrieve risk trajectory history.
- `GET /api/alerts/active` - List unacknowledged emergency safety alerts across all patients.
- `PUT /api/alerts/{alertId}/acknowledge` - Record clinician review and acknowledgment.

### 6. Clinical Reporting (`/api/reports`)
- `GET /api/reports/patient/{patientId}/summary` - Generate structured clinical JSON summary.
- `GET /api/reports/patient/{patientId}/pdf` - Download formal, accredited PDF pulmonary diagnostic report.
- `GET /api/reports/patient/{patientId}/csv` - Stream longitudinal telemetry history as CSV.

### 7. Surveillance Dashboard (`/api/dashboard`)
- `GET /api/dashboard/metrics` - Retrieve population triage metrics, mean $SpO_2$, and active alert counts.

---

## 📄 Certified PDF Report Sample Layout

When downloading a patient report (`GET /api/reports/patient/{id}/pdf`), OpenPDF generates a high-resolution clinical document featuring:
1. **Clinical Header**: Metropolitan Pulmonary Associates letterhead with CLIA ID, medical director details, and report timestamp.
2. **Patient Demographics Table**: MRN, legal name, biological sex, age, height, weight, BMI, smoking status, pack-years, and primary diagnosis.
3. **Current Physiological Status**: Two-column summary comparing pulse oximetry, respiratory rate, and heart rate against symptom logs and mMRC dyspnea grades.
4. **ATS/ERS Spirometric Analysis Table**:
   - Parameters: $FEV_1$, $FVC$, $FEV_1/FVC$, and $PEF$.
   - Columns: Measured value, Predicted normal value, % Predicted, Normal reference range.
   - Ventilatory Pattern badge (*Normal*, *Obstructive*, *Restrictive*, *Mixed*).
   - GOLD COPD stage (*GOLD 1–4*) where applicable.
5. **Clinical Decision Support & Risk Stratification**:
   - Risk Tier banner (*LOW*, *MODERATE*, *HIGH*, *CRITICAL*).
   - Identified physiological and symptomatic risk factors.
   - Evidence-based physician directives (e.g., bronchodilator step-up, sputum culture, emergency transfer).
6. **Physician Attestation & Signature**: Official sign-off block for Dr. Eleanor Vance, MD, FCCP.

---

## 👩‍⚕️ Clinician & Nursing Workflow Guide

```
+-----------------------------------------------------------------------------------------+
|                                    DAILY CLINICAL WORKFLOW                              |
|                                                                                         |
| 1. TRIAGE SURVEILLANCE: Review active red-flag alerts at /api/alerts/active              |
|    - Marcus Vance: SpO2 86%, Tachypnea 30 bpm, Hemoptysis -> Immediate Escalation      |
|                                                                                         |
| 2. PATIENT SELECTION: Navigate to http://localhost:8080/ and select patient chip        |
|    - Review current vital signs (SpO2, RR, AQI) and ATS/ERS spirometry curves          |
|                                                                                         |
| 3. INTAKE & TELEMETRY:                                                                  |
|    - Record new spirometry maneuvers via "Record Vitals & Spirometry" modal             |
|    - Document patient-reported mMRC dyspnea and sputum via "Log Symptoms" modal         |
|                                                                                         |
| 4. CLINICAL DECISION SUPPORT:                                                           |
|    - Inspect automated risk score (0-100) and generated physician directives            |
|                                                                                         |
| 5. DOCUMENTATION:                                                                       |
|    - Click "Download PDF Report" for patient chart or specialist referral               |
|    - Click "Export CSV Telemetry" for longitudinal research and registry submission    |
+-----------------------------------------------------------------------------------------+
```

---

## 🔒 Clinical Quality & Medical Disclaimer

PulmoCare is designed as a **Clinical Decision Support (CDS)** tool intended to augment the professional clinical judgment of licensed healthcare providers. Algorithmic risk calculations, GOLD classifications, and ATS/ERS regressions must be interpreted in conjunction with comprehensive clinical examination, arterial blood gas analysis, imaging, and patient history.
