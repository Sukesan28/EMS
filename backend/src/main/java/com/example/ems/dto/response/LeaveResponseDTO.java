package com.example.ems.dto.response;

import com.example.ems.enums.LeaveStatus;
import com.example.ems.enums.LeaveType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class LeaveResponseDTO {

    private Long id;
    private String employeeCode;
    private String employeeName;
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer totalDays;
    private String reason;
    private LeaveStatus status;
    private String managerComments;
    private LocalDateTime approvedDate;
    private String employeeRole;

    public LeaveResponseDTO() {
    }

    public LeaveResponseDTO(Long id, String employeeCode, String employeeName, LeaveType leaveType, LocalDate startDate, LocalDate endDate, Integer totalDays, String reason, LeaveStatus status, String managerComments, LocalDateTime approvedDateUtc, String employeeRole) {
        this.id = id;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.leaveType = leaveType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalDays = totalDays;
        this.reason = reason;
        this.status = status;
        this.managerComments = managerComments;
        this.approvedDate = convertUtcToIst(approvedDateUtc);
        this.employeeRole = employeeRole;
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

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public LeaveType getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(LeaveType leaveType) {
        this.leaveType = leaveType;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Integer getTotalDays() {
        return totalDays;
    }

    public void setTotalDays(Integer totalDays) {
        this.totalDays = totalDays;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LeaveStatus getStatus() {
        return status;
    }

    public void setStatus(LeaveStatus status) {
        this.status = status;
    }

    public String getManagerComments() {
        return managerComments;
    }

    public void setManagerComments(String managerComments) {
        this.managerComments = managerComments;
    }

    public LocalDateTime getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(LocalDateTime approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getEmployeeRole() {
        return employeeRole;
    }

    public void setEmployeeRole(String employeeRole) {
        this.employeeRole = employeeRole;
    }
}
