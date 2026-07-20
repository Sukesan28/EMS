package com.example.ems.service;

import com.example.ems.dto.request.ClockInRequestDTO;
import com.example.ems.dto.response.AttendanceResponseDTO;
import com.example.ems.entity.Attendance;
import com.example.ems.entity.Employee;
import com.example.ems.entity.LeaveRequest;
import com.example.ems.enums.AttendanceStatus;
import com.example.ems.enums.LeaveStatus;
import com.example.ems.enums.Role;
import com.example.ems.exception.BadRequestException;
import com.example.ems.exception.EmployeeNotFoundException;
import com.example.ems.exception.AttendanceNotFoundException;
import com.example.ems.exception.InvalidAttendanceException;
import com.example.ems.repository.AttendanceRepository;
import com.example.ems.repository.EmployeeRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.ems.repository.LeaveRequestRepository;

@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;

    public AttendanceService(AttendanceRepository attendanceRepository, 
                             EmployeeRepository employeeRepository,
                             LeaveRequestRepository leaveRequestRepository) {
        this.attendanceRepository = attendanceRepository;
        this.employeeRepository = employeeRepository;
        this.leaveRequestRepository = leaveRequestRepository;
    }

    @Transactional
    public AttendanceResponseDTO clockIn(ClockInRequestDTO dto) {
        Employee employee = getCurrentUser();
        LocalDate date = dto.getAttendanceDate() != null ? dto.getAttendanceDate() : LocalDate.now(ZoneId.of("Asia/Kolkata"));

        List<LeaveRequest> approvedLeaves = leaveRequestRepository.findOverlappingLeaves(
                employee,
                List.of(LeaveStatus.APPROVED),
                date,
                date
        );
        if (!approvedLeaves.isEmpty()) {
            throw new InvalidAttendanceException("Leave has already been approved for this date (" + date + "). Attendance is not allowed.");
        }

        Optional<Attendance> existing = attendanceRepository.findByEmployeeAndAttendanceDate(employee, date);
        if (existing.isPresent()) {
            throw new InvalidAttendanceException("Employee has already clocked in for date: " + date);
        }

        Attendance attendance = new Attendance();
        attendance.setEmployee(employee);
        attendance.setAttendanceDate(date);
        attendance.setClockInTime(LocalDateTime.now(ZoneOffset.UTC));
        attendance.setAttendanceStatus(AttendanceStatus.PRESENT);

        Attendance saved = attendanceRepository.save(attendance);
        return mapToResponse(saved);
    }

    @Transactional
    public AttendanceResponseDTO clockOut() {
        Employee employee = getCurrentUser();
        LocalDate date = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        Attendance attendance = attendanceRepository.findByEmployeeAndAttendanceDate(employee, date)
                .orElseThrow(() -> new AttendanceNotFoundException("No clock-in record found for today (" + date + ")"));

        if (attendance.getClockOutTime() != null) {
            throw new InvalidAttendanceException("Employee has already clocked out for today");
        }

        LocalDateTime nowUtc = LocalDateTime.now(ZoneOffset.UTC);
        attendance.setClockOutTime(nowUtc);

        Duration duration = Duration.between(attendance.getClockInTime(), nowUtc);
        double hours = duration.toMillis() / 3600000.0;
        double workingHours = Math.round(hours * 100.0) / 100.0;

        double overtimeHours = 0.0;
        if (workingHours > 8.0) {
            overtimeHours = Math.round((workingHours - 8.0) * 100.0) / 100.0;
        }

        attendance.setWorkingHours(workingHours);
        attendance.setOvertimeHours(overtimeHours);

        if (workingHours >= 8.0) {
            attendance.setAttendanceStatus(AttendanceStatus.PRESENT);
        } else if (workingHours >= 4.0) {
            attendance.setAttendanceStatus(AttendanceStatus.HALF_DAY);
        } else {
            attendance.setAttendanceStatus(AttendanceStatus.ABSENT);
        }

        Attendance saved = attendanceRepository.save(attendance);
        return mapToResponse(saved);
    }

    public List<AttendanceResponseDTO> getMyAttendance() {
        Employee employee = getCurrentUser();
        List<Attendance> list = attendanceRepository.findByEmployeeOrderByAttendanceDateDesc(employee);
        List<AttendanceResponseDTO> dtoList = new ArrayList<>();
        for (Attendance a : list) {
            dtoList.add(mapToResponse(a));
        }
        return dtoList;
    }

    public List<AttendanceResponseDTO> getTeamAttendance() {
        Employee manager = getCurrentUser();
        if (manager.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Access denied. Only MANAGERS can view team attendance.");
        }
        List<Attendance> list = attendanceRepository.findByEmployee_ManagerEmployeeCodeOrderByAttendanceDateDesc(manager.getEmployeeCode());
        List<AttendanceResponseDTO> dtoList = new ArrayList<>();
        for (Attendance a : list) {
            dtoList.add(mapToResponse(a));
        }
        return dtoList;
    }

    public List<AttendanceResponseDTO> getAllAttendance() {
        Employee hr = getCurrentUser();
        if (hr.getRole() != Role.HR) {
            throw new AccessDeniedException("Access denied. Only HR can view all attendance logs.");
        }
        List<Attendance> list = attendanceRepository.findAllByOrderByAttendanceDateDesc();
        List<AttendanceResponseDTO> dtoList = new ArrayList<>();
        for (Attendance a : list) {
            dtoList.add(mapToResponse(a));
        }
        return dtoList;
    }

    private Employee getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        return employeeRepository.findByEmployeeCode(auth.getName())
                .orElseThrow(() -> new EmployeeNotFoundException("Authenticated employee not found"));
    }

    private AttendanceResponseDTO mapToResponse(Attendance attendance) {
        String empCode = attendance.getEmployee() != null ? attendance.getEmployee().getEmployeeCode() : null;
        String empName = attendance.getEmployee() != null ? attendance.getEmployee().getFirstName() + " " + attendance.getEmployee().getLastName() : null;
        return new AttendanceResponseDTO(
                attendance.getAttendanceDate(),
                attendance.getClockInTime(),
                attendance.getClockOutTime(),
                attendance.getWorkingHours(),
                attendance.getOvertimeHours(),
                attendance.getAttendanceStatus(),
                empCode,
                empName
        );
    }
}
