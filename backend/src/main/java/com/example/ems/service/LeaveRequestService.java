package com.example.ems.service;

import com.example.ems.dto.request.LeaveApprovalRequestDTO;
import com.example.ems.dto.request.LeaveRequestDTO;
import com.example.ems.dto.response.LeaveResponseDTO;
import com.example.ems.entity.Employee;
import com.example.ems.entity.LeaveRequest;
import com.example.ems.enums.LeaveStatus;
import com.example.ems.enums.Role;
import com.example.ems.exception.BadRequestException;
import com.example.ems.exception.EmployeeNotFoundException;
import com.example.ems.exception.LeaveNotFoundException;
import com.example.ems.exception.InvalidLeaveException;
import com.example.ems.repository.EmployeeRepository;
import com.example.ems.repository.LeaveRequestRepository;
import com.example.ems.entity.Attendance;
import com.example.ems.repository.AttendanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class LeaveRequestService {

    private static final Logger log = LoggerFactory.getLogger(LeaveRequestService.class);

    private final LeaveRequestRepository leaveRequestRepository;
    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    public LeaveRequestService(LeaveRequestRepository leaveRequestRepository,
                               EmployeeRepository employeeRepository,
                               AttendanceRepository attendanceRepository) {
        this.leaveRequestRepository = leaveRequestRepository;
        this.employeeRepository = employeeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Transactional
    public LeaveResponseDTO applyLeave(LeaveRequestDTO dto) {
        Employee employee = getCurrentUser();
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Kolkata"));

        if (dto.getStartDate().isBefore(today)) {
            throw new InvalidLeaveException("Start date cannot be in the past");
        }
        if (dto.getEndDate().isBefore(dto.getStartDate())) {
            throw new InvalidLeaveException("End date must be greater than or equal to start date");
        }

        List<Attendance> recordedAttendance = attendanceRepository.findAttendanceWithinRange(employee, dto.getStartDate(), dto.getEndDate());
        if (!recordedAttendance.isEmpty()) {
            throw new InvalidLeaveException("Attendance has already been recorded for one or more dates in the requested range. Please correct or remove attendance first.");
        }

        int totalDays = (int) ChronoUnit.DAYS.between(dto.getStartDate(), dto.getEndDate()) + 1;

        List<LeaveRequest> overlaps = leaveRequestRepository.findOverlappingLeaves(
                employee,
                Arrays.asList(LeaveStatus.PENDING, LeaveStatus.APPROVED),
                dto.getStartDate(),
                dto.getEndDate()
        );
        if (!overlaps.isEmpty()) {
            throw new InvalidLeaveException("Selected dates overlap with an existing pending or approved leave request");
        }

        LeaveRequest leave = new LeaveRequest();
        leave.setEmployee(employee);
        leave.setLeaveType(dto.getLeaveType());
        leave.setStartDate(dto.getStartDate());
        leave.setEndDate(dto.getEndDate());
        leave.setTotalDays(totalDays);
        leave.setReason(dto.getReason());
        leave.setStatus(LeaveStatus.PENDING);

        LeaveRequest saved = leaveRequestRepository.save(leave);
        log.info("Leave request applied for employee {} from {} to {}", employee.getEmployeeCode(), dto.getStartDate(), dto.getEndDate());

        return mapToResponse(saved);
    }

    @Transactional
    public LeaveResponseDTO cancelLeave(Long id) {
        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException("Leave request not found with ID: " + id));

        Employee currentUser = getCurrentUser();
        if (!leave.getEmployee().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied. You can only cancel your own leave requests.");
        }

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveException("Only PENDING leave requests can be cancelled");
        }

        leave.setStatus(LeaveStatus.CANCELLED);
        LeaveRequest saved = leaveRequestRepository.save(leave);
        log.info("Leave request ID {} cancelled by employee {}", id, currentUser.getEmployeeCode());
        return mapToResponse(saved);
    }

    public List<LeaveResponseDTO> getMyLeaves() {
        Employee employee = getCurrentUser();
        List<LeaveRequest> list = leaveRequestRepository.findByEmployeeOrderByStartDateDesc(employee);
        List<LeaveResponseDTO> dtoList = new ArrayList<>();
        for (LeaveRequest lr : list) {
            dtoList.add(mapToResponse(lr));
        }
        return dtoList;
    }

    public List<LeaveResponseDTO> getPendingLeaves() {
        Employee manager = getCurrentUser();
        if (manager.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Access denied. Only MANAGERS can view pending logs.");
        }
        List<LeaveRequest> list = leaveRequestRepository.findByEmployee_ManagerEmployeeCodeAndStatusOrderByStartDateDesc(
                manager.getEmployeeCode(), LeaveStatus.PENDING
        );
        List<LeaveResponseDTO> dtoList = new ArrayList<>();
        for (LeaveRequest lr : list) {
            dtoList.add(mapToResponse(lr));
        }
        return dtoList;
    }

    @Transactional
    public LeaveResponseDTO approveLeave(Long id, LeaveApprovalRequestDTO dto) {
        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException("Leave request not found with ID: " + id));

        Employee manager = getCurrentUser();
        verifyLeaveApprovalScope(manager, leave.getEmployee());

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveException("Only PENDING leave requests can be approved");
        }

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setManagerComments(dto.getManagerComments());
        leave.setApprovedDate(LocalDateTime.now(ZoneOffset.UTC));

        LeaveRequest saved = leaveRequestRepository.save(leave);
        log.info("Leave request ID {} APPROVED by manager {}", id, manager.getEmployeeCode());

        return mapToResponse(saved);
    }

    @Transactional
    public LeaveResponseDTO rejectLeave(Long id, LeaveApprovalRequestDTO dto) {
        LeaveRequest leave = leaveRequestRepository.findById(id)
                .orElseThrow(() -> new LeaveNotFoundException("Leave request not found with ID: " + id));

        Employee manager = getCurrentUser();
        verifyLeaveApprovalScope(manager, leave.getEmployee());

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new InvalidLeaveException("Only PENDING leave requests can be rejected");
        }

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setManagerComments(dto.getManagerComments());

        LeaveRequest saved = leaveRequestRepository.save(leave);
        log.info("Leave request ID {} REJECTED by manager {}", id, manager.getEmployeeCode());


        return mapToResponse(saved);
    }

    public List<LeaveResponseDTO> getAllLeaves() {
        Employee hr = getCurrentUser();
        if (hr.getRole() != Role.HR) {
            throw new AccessDeniedException("Access denied. Only HR can view all leave logs.");
        }
        List<LeaveRequest> list = leaveRequestRepository.findAllByOrderByStartDateDesc();
        List<LeaveResponseDTO> dtoList = new ArrayList<>();
        for (LeaveRequest lr : list) {
            dtoList.add(mapToResponse(lr));
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

    private void verifyLeaveApprovalScope(Employee approver, Employee applicant) {
        if (applicant.getRole() == Role.MANAGER || applicant.getRole() == Role.IT_SUPPORT) {
            if (approver.getRole() != Role.HR) {
                throw new AccessDeniedException("Access denied. Only HR can manage leave requests for Managers or IT Support.");
            }
        } else if (applicant.getRole() == Role.EMPLOYEE) {
            if (approver.getRole() != Role.MANAGER || applicant.getManager() == null ||
                    !applicant.getManager().getId().equals(approver.getId())) {
                throw new AccessDeniedException("Access denied. You can only manage leave requests for your direct reports.");
            }
        } else {
            throw new AccessDeniedException("Access denied for this role.");
        }
    }

    private LeaveResponseDTO mapToResponse(LeaveRequest leave) {
        String empName = leave.getEmployee().getFirstName() + " " + leave.getEmployee().getLastName();
        return new LeaveResponseDTO(
                leave.getId(),
                leave.getEmployee().getEmployeeCode(),
                empName,
                leave.getLeaveType(),
                leave.getStartDate(),
                leave.getEndDate(),
                leave.getTotalDays(),
                leave.getReason(),
                leave.getStatus(),
                leave.getManagerComments(),
                leave.getApprovedDate(),
                leave.getEmployee().getRole().name()
        );
    }
}
