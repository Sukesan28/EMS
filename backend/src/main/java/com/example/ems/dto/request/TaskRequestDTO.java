package com.example.ems.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskRequestDTO {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Assignee ID is required")
    private Long assigneeId;

    private java.time.LocalDate dueDate;
    private com.example.ems.enums.TaskStatus status;

    public TaskRequestDTO() {
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

    public java.time.LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(java.time.LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public com.example.ems.enums.TaskStatus getStatus() {
        return status;
    }

    public void setStatus(com.example.ems.enums.TaskStatus status) {
        this.status = status;
    }
}

