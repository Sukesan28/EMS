package com.example.ems.dto.response;

import java.time.LocalDate;

public class PerformanceResponseDTO {

    private Long id;
    private Long employeeId;
    private String employeeCode;
    private String employeeName;
    private String reviewPeriod;
    private String performanceGoal;
    private String achievement;
    private String strength;
    private String improvementArea;
    private Integer rating;
    private String feedback;
    private LocalDate reviewDate;
    private String departmentName;

    public PerformanceResponseDTO() {
    }

    public PerformanceResponseDTO(Long id, Long employeeId, String employeeCode, String employeeName, String reviewPeriod, String performanceGoal, String achievement, String strength, String improvementArea, Integer rating, String feedback, LocalDate reviewDate, String departmentName) {
        this.id = id;
        this.employeeId = employeeId;
        this.employeeCode = employeeCode;
        this.employeeName = employeeName;
        this.reviewPeriod = reviewPeriod;
        this.performanceGoal = performanceGoal;
        this.achievement = achievement;
        this.strength = strength;
        this.improvementArea = improvementArea;
        this.rating = rating;
        this.feedback = feedback;
        this.reviewDate = reviewDate;
        this.departmentName = departmentName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
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

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }
}
