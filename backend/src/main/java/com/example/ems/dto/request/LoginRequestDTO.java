package com.example.ems.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequestDTO {

    @NotBlank(message = "Employee code is required")
    private String employeeCode;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequestDTO() {
    }

    public LoginRequestDTO(String employeeCode, String password) {
        this.employeeCode = employeeCode;
        this.password = password;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
