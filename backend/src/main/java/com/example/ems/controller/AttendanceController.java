package com.example.ems.controller;

import com.example.ems.dto.request.ClockInRequestDTO;
import com.example.ems.dto.response.AttendanceResponseDTO;
import com.example.ems.service.AttendanceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/clock-in")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<AttendanceResponseDTO> clockIn(@Valid @RequestBody ClockInRequestDTO dto) {
        AttendanceResponseDTO response = attendanceService.clockIn(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/clock-out")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<AttendanceResponseDTO> clockOut() {
        AttendanceResponseDTO response = attendanceService.clockOut();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-attendance")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<List<AttendanceResponseDTO>> getMyAttendance() {
        List<AttendanceResponseDTO> response = attendanceService.getMyAttendance();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/team")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<AttendanceResponseDTO>> getTeamAttendance() {
        List<AttendanceResponseDTO> response = attendanceService.getTeamAttendance();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<AttendanceResponseDTO>> getAllAttendance() {
        List<AttendanceResponseDTO> response = attendanceService.getAllAttendance();
        return ResponseEntity.ok(response);
    }
}
