package com.example.ems.controller;

import com.example.ems.dto.request.DepartmentRequestDTO;
import com.example.ems.dto.response.DepartmentNameResponseDTO;
import com.example.ems.dto.response.DepartmentResponseDTO;
import com.example.ems.dto.response.DepartmentManagerHistoryResponseDTO;
import com.example.ems.service.DepartmentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequestDTO dto) {
        DepartmentResponseDTO response = departmentService.createDepartment(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        List<DepartmentResponseDTO> response = departmentService.getAllDepartments();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentById(@PathVariable Long id) {
        DepartmentResponseDTO response = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentRequestDTO dto) {
        DepartmentResponseDTO response = departmentService.updateDepartment(id, dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/change-manager")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<DepartmentResponseDTO> changeManager(
            @PathVariable Long id,
            @RequestParam Long newManagerId) {
        DepartmentResponseDTO response = departmentService.changeManager(id, newManagerId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/manager-history")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<List<DepartmentManagerHistoryResponseDTO>> getManagerHistory(@PathVariable Long id) {
        List<DepartmentManagerHistoryResponseDTO> response = departmentService.getManagerHistory(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dept")
    public ResponseEntity<List<DepartmentNameResponseDTO>> getDepartment() {
        return departmentService.getAllDepartmentName();
    }
}
