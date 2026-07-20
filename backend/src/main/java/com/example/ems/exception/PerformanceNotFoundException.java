package com.example.ems.exception;

public class PerformanceNotFoundException extends ResourceNotFoundException {
    public PerformanceNotFoundException(String message) {
        super(message);
    }
}
