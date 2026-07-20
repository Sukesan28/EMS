package com.example.ems.dto.request;

import com.example.ems.enums.DepartmentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DepartmentRequestDTO {

    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Department name must not exceed 100 characters")
    private String departmentName;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotBlank(message = "Department head name is required")
    private String departmentHead;

    @NotNull(message = "Department status is required")
    private DepartmentStatus status;

    public DepartmentRequestDTO() {
    }

    public DepartmentRequestDTO(String departmentName, String description, String departmentHead, DepartmentStatus status) {
        this.departmentName = departmentName;
        this.description = description;
        this.departmentHead = departmentHead;
        this.status = status;
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
}
