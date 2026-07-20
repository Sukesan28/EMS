package com.example.ems.controller;

import com.example.ems.dto.request.EmployeeRequestDTO;
import com.example.ems.dto.request.EmployeeSelfUpdateDTO;
import com.example.ems.dto.response.EmployeeResponseDTO;
import com.example.ems.dto.response.ManagerNameResponseDTO;
import com.example.ems.dto.response.EmployeeProfileDetailsDTO;
import com.example.ems.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO response = employeeService.createEmployee(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<List<EmployeeResponseDTO>> getAllEmployees(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String role) {

        if (code != null || email != null || department != null || role != null) {
            List<EmployeeResponseDTO> searchResult = employeeService.searchEmployees(code, email, department, role);
            return ResponseEntity.ok(searchResult);
        }
        List<EmployeeResponseDTO> response = employeeService.getAllEmployees();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<EmployeeResponseDTO> getProfile() {
        EmployeeResponseDTO response = employeeService.getProfile();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<EmployeeResponseDTO> selfUpdate(@Valid @RequestBody EmployeeSelfUpdateDTO dto) {
        EmployeeResponseDTO response = employeeService.selfUpdate(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/emp/{id}")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDTO response = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequestDTO dto) {
        EmployeeResponseDTO response = employeeService.updateEmployee(id, dto);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('HR')")
    public ResponseEntity<Map<String, String>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Employee deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/profile-details")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<EmployeeProfileDetailsDTO> getEmployeeProfileDetails(@PathVariable Long id) {
        EmployeeProfileDetailsDTO response = employeeService.getEmployeeProfileDetails(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my-profile-details")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<EmployeeProfileDetailsDTO> getMyProfileDetails() {
        EmployeeResponseDTO currentProfile = employeeService.getProfile();
        EmployeeProfileDetailsDTO response = employeeService.getEmployeeProfileDetails(currentProfile.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/manager")
    public ResponseEntity<List<ManagerNameResponseDTO>> GetManager() {
        return employeeService.getManger();
    }
}
