package com.example.ems.exception;

public class InvalidAttendanceException extends BadRequestException {
    public InvalidAttendanceException(String message) {
        super(message);
    }
}
