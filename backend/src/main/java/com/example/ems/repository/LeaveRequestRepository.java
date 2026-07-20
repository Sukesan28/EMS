package com.example.ems.repository;

import com.example.ems.entity.Employee;
import com.example.ems.entity.LeaveRequest;
import com.example.ems.enums.LeaveStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveRequestRepository extends JpaRepository<LeaveRequest, Long> {
    List<LeaveRequest> findByEmployeeOrderByStartDateDesc(Employee employee);
    List<LeaveRequest> findByEmployee_ManagerEmployeeCodeAndStatusOrderByStartDateDesc(String managerCode, LeaveStatus status);
    List<LeaveRequest> findAllByOrderByStartDateDesc();

    @Query("SELECT l FROM LeaveRequest l WHERE l.employee = :employee AND l.status IN :statuses AND l.startDate <= :endDate AND l.endDate >= :startDate")
    List<LeaveRequest> findOverlappingLeaves(
            @Param("employee") Employee employee,
            @Param("statuses") List<LeaveStatus> statuses,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
