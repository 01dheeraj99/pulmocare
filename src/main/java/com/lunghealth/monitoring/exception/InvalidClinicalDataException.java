package com.lunghealth.monitoring.exception;

public class InvalidClinicalDataException extends RuntimeException {
    public InvalidClinicalDataException(String message) {
        super(message);
    }
}
