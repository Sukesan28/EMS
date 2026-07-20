package com.example.ems.exception;

public class DuplicateDepartmentException extends BadRequestException {
    public DuplicateDepartmentException(String message) {
        super(message);
    }
}
