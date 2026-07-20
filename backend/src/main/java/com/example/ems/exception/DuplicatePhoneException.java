package com.example.ems.exception;

public class DuplicatePhoneException extends BadRequestException {
    public DuplicatePhoneException(String message) {
        super(message);
    }
}
