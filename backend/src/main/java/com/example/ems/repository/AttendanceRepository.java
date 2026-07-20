package com.example.ems.repository;

import com.example.ems.entity.Attendance;
import com.example.ems.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEmployeeAndAttendanceDate(Employee employee, LocalDate attendanceDate);
    List<Attendance> findByEmployeeOrderByAttendanceDateDesc(Employee employee);
    List<Attendance> findByEmployee_ManagerEmployeeCodeOrderByAttendanceDateDesc(String managerCode);
    List<Attendance> findAllByOrderByAttendanceDateDesc();

    @Query("SELECT a FROM Attendance a WHERE a.employee = :employee AND a.attendanceDate BETWEEN :startDate AND :endDate")
    List<Attendance> findAttendanceWithinRange(
            @Param("employee") Employee employee,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
