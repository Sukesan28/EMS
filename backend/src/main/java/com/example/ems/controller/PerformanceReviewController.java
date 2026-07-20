package com.example.ems.controller;

import com.example.ems.dto.request.PerformanceRequestDTO;
import com.example.ems.dto.response.PerformanceResponseDTO;
import com.example.ems.service.PerformanceReviewService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
public class PerformanceReviewController {

    private final PerformanceReviewService performanceReviewService;

    public PerformanceReviewController(PerformanceReviewService performanceReviewService) {
        this.performanceReviewService = performanceReviewService;
    }

    @PostMapping("/{employeeId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PerformanceResponseDTO> createReview(
            @PathVariable Long employeeId,
            @Valid @RequestBody PerformanceRequestDTO dto) {
        PerformanceResponseDTO response = performanceReviewService.createReview(employeeId, dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{reviewId}")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<PerformanceResponseDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody PerformanceRequestDTO dto) {
        PerformanceResponseDTO response = performanceReviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-reviews")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<List<PerformanceResponseDTO>> getMyReviews() {
        List<PerformanceResponseDTO> response = performanceReviewService.getMyReviews();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<PerformanceResponseDTO>> getTeamReviews() {
        List<PerformanceResponseDTO> response = performanceReviewService.getTeamReviews();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<PerformanceResponseDTO>> getAllReviews() {
        List<PerformanceResponseDTO> response = performanceReviewService.getAllReviews();
        return ResponseEntity.ok(response);
    }
}
