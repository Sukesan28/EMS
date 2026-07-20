package com.example.ems.dto.response;

import com.example.ems.enums.AttendanceStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class AttendanceResponseDTO {

    private LocalDate attendanceDate;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private Double workingHours;
    private Double overtimeHours;
    private AttendanceStatus attendanceStatus;
    private String employeeCode;
    private String employeeName;

    public AttendanceResponseDTO() {
    }

    public AttendanceResponseDTO(LocalDate attendanceDate, LocalDateTime clockInTimeUtc, LocalDateTime clockOutTimeUtc, Double workingHours, Double overtimeHours, AttendanceStatus attendanceStatus) {
        this.attendanceDate = attendanceDate;
        this.clockInTime = convertUtcToIst(clockInTimeUtc);
        this.clockOutTime = convertUtcToIst(clockOutTimeUtc);
        this.workingHours = workingHours;
        this.overtimeHours = overtimeHours;
        this.attendanceStatus = attendanceStatus;
    }

    public AttendanceResponseDTO(LocalDate attendanceDate, LocalDateTime clockInTimeUtc, LocalDateTime clockOutTimeUtc, Double workingHours, Double overtimeHours, AttendanceStatus attendanceStatus, String employeeCode, String employeeName) {
        this.attendanceDate = attendanceDate;
        this.clockInTime = convertUtcToIst(clockInTimeUtc);
        this.clockOutTime = convertUtcToIst(clockOutTimeUtc);
        this.workingHours = workingHours;
        this.overtimeHours = overtimeHours;
        this.attendanceStatus = attendanceStatus;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
    }

    private LocalDateTime convertUtcToIst(LocalDateTime utcDateTime) {
        if (utcDateTime == null) {
            return null;
        }
        return utcDateTime.atZone(ZoneOffset.UTC)
                .withZoneSameInstant(ZoneId.of("Asia/Kolkata"))
                .toLocalDateTime();
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalDateTime getClockInTime() {
        return clockInTime;
    }

    public void setClockInTime(LocalDateTime clockInTime) {
        this.clockInTime = clockInTime;
    }

    public LocalDateTime getClockOutTime() {
        return clockOutTime;
    }

    public void setClockOutTime(LocalDateTime clockOutTime) {
        this.clockOutTime = clockOutTime;
    }

    public Double getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(Double workingHours) {
        this.workingHours = workingHours;
    }

    public Double getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(Double overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public AttendanceStatus getAttendanceStatus() {
        return attendanceStatus;
    }

    public void setAttendanceStatus(AttendanceStatus attendanceStatus) {
        this.attendanceStatus = attendanceStatus;
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
}
