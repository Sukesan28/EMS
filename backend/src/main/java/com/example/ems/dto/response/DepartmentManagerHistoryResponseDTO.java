package com.example.ems.dto.response;

import java.time.LocalDateTime;

public class DepartmentManagerHistoryResponseDTO {

    private Long id;
    private Long departmentId;
    private String previousManagerName;
    private String newManagerName;
    private LocalDateTime changedAt;

    public DepartmentManagerHistoryResponseDTO() {
    }

    public DepartmentManagerHistoryResponseDTO(Long id, Long departmentId, String previousManagerName, String newManagerName, LocalDateTime changedAt) {
        this.id = id;
        this.departmentId = departmentId;
        this.previousManagerName = previousManagerName;
        this.newManagerName = newManagerName;
        this.changedAt = changedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public String getPreviousManagerName() {
        return previousManagerName;
    }

    public void setPreviousManagerName(String previousManagerName) {
        this.previousManagerName = previousManagerName;
    }

    public String getNewManagerName() {
        return newManagerName;
    }

    public void setNewManagerName(String newManagerName) {
        this.newManagerName = newManagerName;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}
