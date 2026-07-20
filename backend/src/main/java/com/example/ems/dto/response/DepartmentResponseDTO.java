package com.example.ems.dto.response;

import com.example.ems.enums.DepartmentStatus;

public class DepartmentResponseDTO {

    private Long id;
    private String departmentCode;
    private String departmentName;
    private String description;
    private String departmentHead;
    private DepartmentStatus status;
    private Long totalEmployees;

    public DepartmentResponseDTO() {
    }

    public DepartmentResponseDTO(Long id, String departmentCode, String departmentName, String description, String departmentHead, DepartmentStatus status, Long totalEmployees) {
        this.id = id;
        this.departmentCode = departmentCode;
        this.departmentName = departmentName;
        this.description = description;
        this.departmentHead = departmentHead;
        this.status = status;
        this.totalEmployees = totalEmployees;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartmentCode() {
        return departmentCode;
    }

    public void setDepartmentCode(String departmentCode) {
        this.departmentCode = departmentCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDepartmentHead() {
        return departmentHead;
    }

    public void setDepartmentHead(String departmentHead) {
        this.departmentHead = departmentHead;
    }

    public DepartmentStatus getStatus() {
        return status;
    }

    public void setStatus(DepartmentStatus status) {
        this.status = status;
    }

    public Long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(Long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }
}
