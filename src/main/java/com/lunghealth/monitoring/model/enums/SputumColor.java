package com.lunghealth.monitoring.model.enums;

/**
 * Tracheobronchial expectorate characterization according to Anthonisen criteria.
 *
 * <p>Sputum purulence is a primary clinical determinant of bacterial exacerbations
 * in COPD and bronchiectasis, guiding the initiation of targeted antibiotic therapy.</p>
 */
public enum SputumColor {
    NONE("No sputum produced"),
    CLEAR_OR_WHITE("Mucoid: Clear or white (typically non-infectious / baseline)"),
    YELLOW("Mucopurulent: Yellowish (early neutrophil infiltration / suspected infection)"),
    GREEN("Purulent: Green (heavy myeloperoxidase activity, active bacterial colonization)"),
    RUST_BROWN("Rust-brown / Prune juice (classic alveolar involvement, e.g., pneumococcal)"),
    BLOOD_STAINED("Blood-streaked or frank hemoptysis (immediate clinical red flag)");

    private final String clinicalNote;

    SputumColor(String clinicalNote) {
        this.clinicalNote = clinicalNote;
    }

    public String getClinicalNote() {
        return clinicalNote;
    }
}
