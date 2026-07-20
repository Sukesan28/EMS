package com.example.ems.exception;

public class DuplicateEmailException extends BadRequestException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
