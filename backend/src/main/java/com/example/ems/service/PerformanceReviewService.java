package com.example.ems.service;

import com.example.ems.dto.request.PerformanceRequestDTO;
import com.example.ems.dto.response.PerformanceResponseDTO;
import com.example.ems.entity.Employee;
import com.example.ems.entity.PerformanceReview;
import com.example.ems.enums.Role;
import com.example.ems.exception.BadRequestException;
import com.example.ems.exception.EmployeeNotFoundException;
import com.example.ems.exception.PerformanceNotFoundException;
import com.example.ems.repository.EmployeeRepository;
import com.example.ems.repository.PerformanceReviewRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@org.springframework.transaction.annotation.Transactional(readOnly = true)
public class PerformanceReviewService {

    private static final Logger log = LoggerFactory.getLogger(PerformanceReviewService.class);

    private final PerformanceReviewRepository performanceReviewRepository;
    private final EmployeeRepository employeeRepository;

    public PerformanceReviewService(PerformanceReviewRepository performanceReviewRepository,
                                  EmployeeRepository employeeRepository) {
        this.performanceReviewRepository = performanceReviewRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public PerformanceResponseDTO createReview(Long employeeId, PerformanceRequestDTO dto) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        Employee manager = employeeRepository.findByEmployeeCode(auth.getName())
                .orElseThrow(() -> new EmployeeNotFoundException("Authenticated employee not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Access denied. Only MANAGERS can perform this action.");
        }
        if (employee.getManager() == null ||
                !employee.getManager().getEmployeeCode().equals(manager.getEmployeeCode())) {
            throw new AccessDeniedException("Access denied. You can only record performance reviews for your direct reports.");
        }

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        if (dto.getReviewDate().isAfter(today)) {
            throw new BadRequestException("Review date cannot be in the future");
        }

        if (performanceReviewRepository.existsByEmployeeAndReviewPeriod(employee, dto.getReviewPeriod())) {
            throw new BadRequestException("Performance review already exists for this period: " + dto.getReviewPeriod());
        }

        PerformanceReview review = new PerformanceReview();
        review.setEmployee(employee);
        review.setReviewPeriod(dto.getReviewPeriod());
        review.setPerformanceGoal(dto.getPerformanceGoal());
        review.setAchievement(dto.getAchievement());
        review.setStrength(dto.getStrength());
        review.setImprovementArea(dto.getImprovementArea());
        review.setRating(dto.getRating());
        review.setFeedback(dto.getFeedback());
        review.setReviewDate(dto.getReviewDate());

        PerformanceReview saved = performanceReviewRepository.save(review);
        log.info("Performance review created for employee: {} by manager: {}", employee.getEmployeeCode(), manager.getEmployeeCode());
        return mapToResponse(saved);
    }

    @Transactional
    public PerformanceResponseDTO updateReview(Long reviewId, PerformanceRequestDTO dto) {

        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new PerformanceNotFoundException("Performance review not found with ID: " + reviewId));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        Employee manager = employeeRepository.findByEmployeeCode(auth.getName())
                .orElseThrow(() -> new EmployeeNotFoundException("Authenticated employee not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Access denied. Only MANAGERS can perform this action.");
        }
        if (review.getEmployee().getManager() == null ||
                !review.getEmployee().getManager().getEmployeeCode().equals(manager.getEmployeeCode())) {
            throw new AccessDeniedException("Access denied. You can only record performance reviews for your direct reports.");
        }


        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));
        if (dto.getReviewDate().isAfter(today)) {
            throw new BadRequestException("Review date cannot be in the future");
        }

        if (performanceReviewRepository.existsByEmployeeAndReviewPeriodAndIdNot(review.getEmployee(), dto.getReviewPeriod(), reviewId)) {
            throw new BadRequestException("Performance review already exists for this period");
        }

        review.setReviewPeriod(dto.getReviewPeriod());
        review.setPerformanceGoal(dto.getPerformanceGoal());
        review.setAchievement(dto.getAchievement());
        review.setStrength(dto.getStrength());
        review.setImprovementArea(dto.getImprovementArea());
        review.setRating(dto.getRating());
        review.setFeedback(dto.getFeedback());
        review.setReviewDate(dto.getReviewDate());

        PerformanceReview updated = performanceReviewRepository.save(review);
        log.info("Performance review ID {} updated by manager: {}", reviewId, manager.getEmployeeCode());

        return mapToResponse(updated);
    }

    public List<PerformanceResponseDTO> getMyReviews() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        Employee employee = employeeRepository.findByEmployeeCode(auth.getName())
                .orElseThrow(() -> new EmployeeNotFoundException("Authenticated employee not found"));

        List<PerformanceReview> list = performanceReviewRepository.findByEmployeeOrderByReviewDateDesc(employee);

        List<PerformanceResponseDTO> dtoList = new ArrayList<>();
        for (PerformanceReview pr : list) {
            dtoList.add(mapToResponse(pr));
        }
        return dtoList;
    }

    public List<PerformanceResponseDTO> getTeamReviews() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        Employee manager = employeeRepository.findByEmployeeCode(auth.getName())
                .orElseThrow(() -> new EmployeeNotFoundException("Authenticated employee not found"));

        if (manager.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Access denied. Only MANAGERS can view team appraisals.");
        }

        List<PerformanceReview> list = performanceReviewRepository.findByEmployee_ManagerEmployeeCodeOrderByReviewDateDesc(manager.getEmployeeCode());

        List<PerformanceResponseDTO> dtoList = new ArrayList<>();
        for (PerformanceReview pr : list) {
            dtoList.add(mapToResponse(pr));
        }
        return dtoList;
    }

    public List<PerformanceResponseDTO> getAllReviews() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        Employee hr = employeeRepository.findByEmployeeCode(auth.getName())
                .orElseThrow(() -> new EmployeeNotFoundException("Authenticated employee not found"));

        if (hr.getRole() != Role.HR) {
            throw new AccessDeniedException("Access denied. Only HR can view all appraisal reviews.");
        }
        List<PerformanceReview> list = performanceReviewRepository.findAllByOrderByReviewDateDesc();

        List<PerformanceResponseDTO> dtoList = new ArrayList<>();
        for (PerformanceReview pr : list) {
            dtoList.add(mapToResponse(pr));
        }
        return dtoList;
    }

    private PerformanceResponseDTO mapToResponse(PerformanceReview review) {
        String empName = review.getEmployee().getFirstName() + " " + review.getEmployee().getLastName();
        String deptName = review.getEmployee().getDepartment() != null ? review.getEmployee().getDepartment().getDepartmentName() : "None";
        return new PerformanceResponseDTO(
                review.getId(),
                review.getEmployee().getId(),
                review.getEmployee().getEmployeeCode(),
                empName,
                review.getReviewPeriod(),
                review.getPerformanceGoal(),
                review.getAchievement(),
                review.getStrength(),
                review.getImprovementArea(),
                review.getRating(),
                review.getFeedback(),
                review.getReviewDate(),
                deptName
        );
    }
}
