package com.example.ems.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "performance_reviews")
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "review_period", nullable = false)
    private String reviewPeriod;

    @Column(name = "performance_goal", nullable = false, length = 1000)
    private String performanceGoal;

    @Column(length = 1000)
    private String achievement;

    @Column(length = 1000)
    private String strength;

    @Column(name = "improvement_area", length = 1000)
    private String improvementArea;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 2000)
    private String feedback;

    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now(ZoneOffset.UTC);
    }

    public PerformanceReview() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public String getReviewPeriod() {
        return reviewPeriod;
    }

    public void setReviewPeriod(String reviewPeriod) {
        this.reviewPeriod = reviewPeriod;
    }

    public String getPerformanceGoal() {
        return performanceGoal;
    }

    public void setPerformanceGoal(String performanceGoal) {
        this.performanceGoal = performanceGoal;
    }

    public String getAchievement() {
        return achievement;
    }

    public void setAchievement(String achievement) {
        this.achievement = achievement;
    }

    public String getStrength() {
        return strength;
    }

    public void setStrength(String strength) {
        this.strength = strength;
    }

    public String getImprovementArea() {
        return improvementArea;
    }

    public void setImprovementArea(String improvementArea) {
        this.improvementArea = improvementArea;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDate getReviewDate() {
        return reviewDate;
    }

    public void setReviewDate(LocalDate reviewDate) {
        this.reviewDate = reviewDate;
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
}
