package com.example.ems.exception;

public class DepartmentNotFoundException extends ResourceNotFoundException {
    public DepartmentNotFoundException(String message) {
        super(message);
    }
}
