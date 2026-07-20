package com.example.ems.exception;

public class InvalidLeaveException extends BadRequestException {
    public InvalidLeaveException(String message) {
        super(message);
    }
}
