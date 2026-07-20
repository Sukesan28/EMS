package com.example.ems.exception;

public class EmployeeNotFoundException extends ResourceNotFoundException {
    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
