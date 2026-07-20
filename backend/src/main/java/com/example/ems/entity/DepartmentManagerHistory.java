package com.example.ems.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "department_manager_history")
public class DepartmentManagerHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @Column(name = "previous_manager_name")
    private String previousManagerName;

    @Column(name = "new_manager_name")
    private String newManagerName;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public DepartmentManagerHistory() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
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
