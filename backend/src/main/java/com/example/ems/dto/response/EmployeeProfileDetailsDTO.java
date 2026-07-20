package com.example.ems.dto.response;

import java.util.List;

public class EmployeeProfileDetailsDTO {

    private EmployeeResponseDTO employeeDetails;
    private List<PerformanceResponseDTO> performanceSummary;
    private List<AttendanceResponseDTO> attendanceSummary;
    private List<LeaveResponseDTO> leaveSummary;

    public EmployeeProfileDetailsDTO() {
    }

    public EmployeeProfileDetailsDTO(EmployeeResponseDTO employeeDetails,
                                     List<PerformanceResponseDTO> performanceSummary,
                                     List<AttendanceResponseDTO> attendanceSummary,
                                     List<LeaveResponseDTO> leaveSummary) {
        this.employeeDetails = employeeDetails;
        this.performanceSummary = performanceSummary;
        this.attendanceSummary = attendanceSummary;
        this.leaveSummary = leaveSummary;
    }

    public EmployeeResponseDTO getEmployeeDetails() {
        return employeeDetails;
    }

    public void setEmployeeDetails(EmployeeResponseDTO employeeDetails) {
        this.employeeDetails = employeeDetails;
    }

    public List<PerformanceResponseDTO> getPerformanceSummary() {
        return performanceSummary;
    }

    public void setPerformanceSummary(List<PerformanceResponseDTO> performanceSummary) {
        this.performanceSummary = performanceSummary;
    }

    public List<AttendanceResponseDTO> getAttendanceSummary() {
        return attendanceSummary;
    }

    public void setAttendanceSummary(List<AttendanceResponseDTO> attendanceSummary) {
        this.attendanceSummary = attendanceSummary;
    }

    public List<LeaveResponseDTO> getLeaveSummary() {
        return leaveSummary;
    }

    public void setLeaveSummary(List<LeaveResponseDTO> leaveSummary) {
        this.leaveSummary = leaveSummary;
    }
}
