package com.example.ems.dto.response;

import com.example.ems.enums.EmploymentStatus;
import com.example.ems.enums.Gender;
import com.example.ems.enums.Role;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class EmployeeResponseDTO {

    private Long id;
    private String employeeCode;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Gender gender;
    private LocalDate dateOfBirth;
    private LocalDate joiningDate;
    private String designation;
    private Double salary;
    private String address;
    private EmploymentStatus employmentStatus;
    private Role role;
    private String departmentName;
    private String managerName;
    private Boolean accountEnabled;
    private Boolean accountLocked;
    private Boolean firstLogin;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeResponseDTO() {
    }

    public EmployeeResponseDTO(Long id, String employeeCode, String firstName, String lastName, String email, String phone, Gender gender, LocalDate dateOfBirth, LocalDate joiningDate, String designation, Double salary, String address, EmploymentStatus employmentStatus, Role role, String departmentName, String managerName, Boolean accountEnabled, Boolean accountLocked, Boolean firstLogin, LocalDateTime createdAtUtc, LocalDateTime updatedAtUtc) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.joiningDate = joiningDate;
        this.designation = designation;
        this.salary = salary;
        this.address = address;
        this.employmentStatus = employmentStatus;
        this.role = role;
        this.departmentName = departmentName;
        this.managerName = managerName;
        this.accountEnabled = accountEnabled;
        this.accountLocked = accountLocked;
        this.firstLogin = firstLogin;
        this.createdAt = convertUtcToIst(createdAtUtc);
        this.updatedAt = convertUtcToIst(updatedAtUtc);
    }

    private LocalDateTime convertUtcToIst(LocalDateTime utcDateTime) {
        if (utcDateTime == null) {
            return null;
        }
        return utcDateTime.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
                .toLocalDateTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public LocalDate getJoiningDate() {
        return joiningDate;
    }

    public void setJoiningDate(LocalDate joiningDate) {
        this.joiningDate = joiningDate;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public EmploymentStatus getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(EmploymentStatus employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }

    public Boolean getAccountEnabled() {
        return accountEnabled;
    }

    public void setAccountEnabled(Boolean accountEnabled) {
        this.accountEnabled = accountEnabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getAccountLocked() {
        return accountLocked;
    }

    public void setAccountLocked(Boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public Boolean getFirstLogin() {
        return firstLogin;
    }

    public void setFirstLogin(Boolean firstLogin) {
        this.firstLogin = firstLogin;
    }
}
