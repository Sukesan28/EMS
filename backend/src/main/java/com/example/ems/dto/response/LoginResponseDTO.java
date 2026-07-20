package com.example.ems.dto.response;

public class LoginResponseDTO {

    private String employeeCode;
    private String fullName;
    private String role;
    private Boolean firstLogin;
    private String message;
    private String token;

    public LoginResponseDTO() {
    }

    public LoginResponseDTO(String employeeCode, String fullName, String role, Boolean firstLogin, String message) {
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.role = role;
        this.firstLogin = firstLogin;
        this.message = message;
    }

    public LoginResponseDTO(String employeeCode, String fullName, String role, Boolean firstLogin, String message, String token) {
        this.employeeCode = employeeCode;
        this.fullName = fullName;
        this.role = role;
        this.firstLogin = firstLogin;
        this.message = message;
        this.token = token;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
