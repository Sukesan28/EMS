package com.example.ems.dto.response;

import java.time.LocalDateTime;

public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Long assigneeId;
    private String assigneeName;
    private String assigneeCode;
    private String departmentName;
    private Long assignerId;
    private String assignerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private com.example.ems.enums.TaskStatus status;
    private java.time.LocalDate dueDate;

    public TaskResponseDTO() {
    }

    public TaskResponseDTO(Long id, String title, String description,
                           Long assigneeId, String assigneeName, String assigneeCode, String departmentName,
                           Long assignerId, String assignerName,
                           LocalDateTime createdAt, LocalDateTime updatedAt,
                           com.example.ems.enums.TaskStatus status, java.time.LocalDate dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.assigneeId = assigneeId;
        this.assigneeName = assigneeName;
        this.assigneeCode = assigneeCode;
        this.departmentName = departmentName;
        this.assignerId = assignerId;
        this.assignerName = assignerName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.dueDate = dueDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getAssigneeId() {
        return assigneeId;
    }

    public void setAssigneeId(Long assigneeId) {
        this.assigneeId = assigneeId;
    }

    public String getAssigneeName() {
        return assigneeName;
    }

    public void setAssigneeName(String assigneeName) {
        this.assigneeName = assigneeName;
    }

    public String getAssigneeCode() {
        return assigneeCode;
    }

    public void setAssigneeCode(String assigneeCode) {
        this.assigneeCode = assigneeCode;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public Long getAssignerId() {
        return assignerId;
    }

    public void setAssignerId(Long assignerId) {
        this.assignerId = assignerId;
    }

    public String getAssignerName() {
        return assignerName;
    }

    public void setAssignerName(String assignerName) {
        this.assignerName = assignerName;
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

    public com.example.ems.enums.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(com.example.ems.enums.TaskStatus status) {
        this.status = status;
    }

    public java.time.LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(java.time.LocalDate dueDate) {
        this.dueDate = dueDate;
    }
}
