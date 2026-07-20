package com.example.ems.exception;

public class LeaveNotFoundException extends ResourceNotFoundException {
    public LeaveNotFoundException(String message) {
        super(message);
    }
}
