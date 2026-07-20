package com.example.ems.controller;

import com.example.ems.dto.request.LeaveApprovalRequestDTO;
import com.example.ems.dto.request.LeaveRequestDTO;
import com.example.ems.dto.response.LeaveResponseDTO;
import com.example.ems.service.LeaveRequestService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;

    public LeaveRequestController(LeaveRequestService leaveRequestService) {
        this.leaveRequestService = leaveRequestService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<LeaveResponseDTO> applyLeave(@Valid @RequestBody LeaveRequestDTO dto) {
        LeaveResponseDTO response = leaveRequestService.applyLeave(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<LeaveResponseDTO> cancelLeave(@PathVariable Long id) {
        LeaveResponseDTO response = leaveRequestService.cancelLeave(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-leaves")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<List<LeaveResponseDTO>> getMyLeaves() {
        List<LeaveResponseDTO> response = leaveRequestService.getMyLeaves();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<List<LeaveResponseDTO>> getPendingLeaves() {
        List<LeaveResponseDTO> response = leaveRequestService.getPendingLeaves();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<LeaveResponseDTO> approveLeave(@PathVariable Long id, @RequestBody LeaveApprovalRequestDTO dto) {
        LeaveResponseDTO response = leaveRequestService.approveLeave(id, dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER', 'HR')")
    public ResponseEntity<LeaveResponseDTO> rejectLeave(@PathVariable Long id, @RequestBody LeaveApprovalRequestDTO dto) {
        LeaveResponseDTO response = leaveRequestService.rejectLeave(id, dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<LeaveResponseDTO>> getAllLeaves() {
        List<LeaveResponseDTO> response = leaveRequestService.getAllLeaves();
        return ResponseEntity.ok(response);
    }
}
