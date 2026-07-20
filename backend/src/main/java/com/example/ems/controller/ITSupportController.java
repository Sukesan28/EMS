package com.example.ems.controller;

import com.example.ems.dto.response.EmployeeResponseDTO;
import com.example.ems.service.ITSupportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/it-support")
@PreAuthorize("hasRole('IT_SUPPORT')")
public class ITSupportController {

    private final ITSupportService itSupportService;

    public ITSupportController(ITSupportService itSupportService) {
        this.itSupportService = itSupportService;
    }

    @PutMapping("/reset-password/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> resetPassword(@PathVariable Long employeeId) {
        EmployeeResponseDTO response = itSupportService.resetPassword(employeeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/unlock-account/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> unlockAccount(@PathVariable Long employeeId) {
        EmployeeResponseDTO response = itSupportService.unlockAccount(employeeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/enable-account/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> enableAccount(@PathVariable Long employeeId) {
        EmployeeResponseDTO response = itSupportService.enableAccount(employeeId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/disable-account/{employeeId}")
    public ResponseEntity<EmployeeResponseDTO> disableAccount(@PathVariable Long employeeId) {
        EmployeeResponseDTO response = itSupportService.disableAccount(employeeId);
        return ResponseEntity.ok(response);
    }
}
