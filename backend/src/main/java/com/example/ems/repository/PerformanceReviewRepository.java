package com.example.ems.repository;

import com.example.ems.entity.Employee;
import com.example.ems.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployeeOrderByReviewDateDesc(Employee employee);
    List<PerformanceReview> findByEmployee_ManagerEmployeeCodeOrderByReviewDateDesc(String managerCode);
    List<PerformanceReview> findAllByOrderByReviewDateDesc();
    boolean existsByEmployeeAndReviewPeriod(Employee employee, String reviewPeriod);
    boolean existsByEmployeeAndReviewPeriodAndIdNot(Employee employee, String reviewPeriod, Long id);
}
