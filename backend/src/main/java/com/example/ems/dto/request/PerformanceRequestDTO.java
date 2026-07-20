package com.example.ems.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class PerformanceRequestDTO {

    @NotBlank(message = "Review period is required")
    private String reviewPeriod;

    @NotBlank(message = "Performance Goal is required")
    private String performanceGoal;

    private String achievement;
    private String strength;
    private String improvementArea;

    @NotNull(message = "Rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot exceed 5")
    private Integer rating;

    private String feedback;

    @NotNull(message = "Review date is required")
    private LocalDate reviewDate;

    public PerformanceRequestDTO() {
    }

    public PerformanceRequestDTO(String reviewPeriod, String performanceGoal, String achievement, String strength, String improvementArea, Integer rating, String feedback, LocalDate reviewDate) {
        this.reviewPeriod = reviewPeriod;
        this.performanceGoal = performanceGoal;
        this.achievement = achievement;
        this.strength = strength;
        this.improvementArea = improvementArea;
        this.rating = rating;
        this.feedback = feedback;
        this.reviewDate = reviewDate;
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
}
