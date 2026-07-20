package com.example.ems.service;

import com.example.ems.dto.request.DepartmentRequestDTO;
import com.example.ems.dto.response.DepartmentNameResponseDTO;
import com.example.ems.dto.response.DepartmentResponseDTO;
import com.example.ems.dto.response.DepartmentManagerHistoryResponseDTO;
import com.example.ems.entity.Department;
import com.example.ems.entity.Employee;
import com.example.ems.entity.DepartmentManagerHistory;
import com.example.ems.enums.Role;
import com.example.ems.exception.DepartmentNotFoundException;
import com.example.ems.exception.DuplicateDepartmentException;
import com.example.ems.exception.EmployeeNotFoundException;
import com.example.ems.exception.BadRequestException;
import com.example.ems.repository.DepartmentRepository;
import com.example.ems.repository.EmployeeRepository;
import com.example.ems.repository.DepartmentManagerHistoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentManagerHistoryRepository departmentManagerHistoryRepository;

    public DepartmentService(DepartmentRepository departmentRepository,
                             EmployeeRepository employeeRepository,
                             DepartmentManagerHistoryRepository departmentManagerHistoryRepository) {
        this.departmentRepository = departmentRepository;
        this.employeeRepository = employeeRepository;
        this.departmentManagerHistoryRepository = departmentManagerHistoryRepository;
    }

    @Transactional
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO dto) {
        // Step 1: Validate Department Name Unique
        if (departmentRepository.existsByDepartmentName(dto.getDepartmentName())) {
            throw new DuplicateDepartmentException("Department already exists with name: " + dto.getDepartmentName());
        }

        // Step 2: Generate Department Code
        String prefix = derivePrefix(dto.getDepartmentName());
        String code = generateDepartmentCode(prefix);

        // Step 3: Map Request to Entity & Save
        Department dept = new Department(
                code,
                dto.getDepartmentName(),
                dto.getDescription(),
                dto.getDepartmentHead(),
                dto.getStatus()
        );

        Department saved = departmentRepository.save(dept);

        // Step 4: Map Entity to Response DTO & Return
        return mapToResponse(saved, 0L);
    }

    public List<DepartmentResponseDTO> getAllDepartments() {
        // Step 1: Fetch All Departments
        List<Department> departments = departmentRepository.findAll();
        List<DepartmentResponseDTO> dtoList = new ArrayList<>();

        // Step 2: Fetch Employee Count for Each Department & Map to DTO
        for (Department dept : departments) {
            long count = employeeRepository.countByDepartmentId(dept.getId());
            dtoList.add(mapToResponse(dept, count));
        }

        // Step 3: Return Response List
        return dtoList;
    }

    public DepartmentResponseDTO getDepartmentById(Long id) {
        // Step 1: Fetch Department Record
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with ID: " + id));

        // Step 2: Fetch Employee Count
        long count = employeeRepository.countByDepartmentId(dept.getId());

        // Step 3: Map to Response DTO & Return
        return mapToResponse(dept, count);
    }

    @Transactional
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentRequestDTO dto) {
        // Step 1: Fetch Existing Department
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with ID: " + id));

        // Step 2: Validate Department Name Unique if Changed
        if (!dept.getDepartmentName().equalsIgnoreCase(dto.getDepartmentName()) &&
                departmentRepository.existsByDepartmentName(dto.getDepartmentName())) {
            throw new DuplicateDepartmentException("Department already exists with name: " + dto.getDepartmentName());
        }

        // Step 3: Apply Business Logic / Update Fields
        dept.setDepartmentName(dto.getDepartmentName());
        dept.setDescription(dto.getDescription());
        dept.setDepartmentHead(dto.getDepartmentHead());
        dept.setStatus(dto.getStatus());

        // Step 4: Save Updated Department
        Department updated = departmentRepository.save(dept);
        long count = employeeRepository.countByDepartmentId(updated.getId());

        // Step 5: Map to Response DTO & Return
        return mapToResponse(updated, count);
    }

    @Transactional
    public DepartmentResponseDTO changeManager(Long departmentId, Long newManagerId) {
        // Step 1: Validate & Fetch Department
        Department dept = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with ID: " + departmentId));

        // Step 2: Fetch Target New Manager
        Employee newManager = employeeRepository.findById(newManagerId)
                .orElseThrow(() -> new EmployeeNotFoundException("New manager not found with ID: " + newManagerId));

        // Step 3: Validate the selected employee is eligible (not HR or IT_SUPPORT)
        if (newManager.getRole() == Role.HR || newManager.getRole() == Role.IT_SUPPORT) {
            throw new BadRequestException("HR and IT Support roles cannot be assigned as department managers.");
        }

        // Step 4: Auto-transfer employee to the target department (allows cross-department promotion)
        newManager.setDepartment(dept);

        // Step 5: Promote to MANAGER role
        newManager.setRole(Role.MANAGER);

        // Step 4: Fetch Current Department Manager (Old Manager)
        List<Employee> currentManagers = employeeRepository.findByDepartmentIdAndRole(departmentId, Role.MANAGER);
        Employee oldManager = null;
        for (Employee m : currentManagers) {
            if (!m.getId().equals(newManagerId)) {
                oldManager = m;
                break;
            }
        }

        // Step 5: Demote old manager & reassign reporting team to new manager
        String oldManagerName = "None";
        if (oldManager != null) {
            oldManagerName = oldManager.getFirstName() + " " + oldManager.getLastName();
            
            // Demote old manager to EMPLOYEE role
            oldManager.setRole(Role.EMPLOYEE);
            // Old manager now reports to the new manager
            oldManager.setManager(newManager);
            employeeRepository.save(oldManager);

            // Reassign direct reports to the new manager
            List<Employee> reports = employeeRepository.findByManagerId(oldManager.getId());
            for (Employee report : reports) {
                report.setManager(newManager);
                employeeRepository.save(report);
            }
        }

        // Assign new manager's reporter as HR
        List<Employee> hrs = employeeRepository.findByRole(Role.HR);
        if (!hrs.isEmpty()) {
            newManager.setManager(hrs.get(0));
        } else {
            newManager.setManager(null);
        }
        employeeRepository.save(newManager);

        // Step 6: Update Department Head field
        dept.setDepartmentHead(newManager.getFirstName() + " " + newManager.getLastName());
        Department savedDept = departmentRepository.save(dept);

        // Step 7: Record Manager History
        DepartmentManagerHistory history = new DepartmentManagerHistory();
        history.setDepartment(dept);
        history.setPreviousManagerName(oldManagerName);
        history.setNewManagerName(newManager.getFirstName() + " " + newManager.getLastName());
        history.setChangedAt(LocalDateTime.now(ZoneOffset.UTC));
        departmentManagerHistoryRepository.save(history);

        // Step 8: Return Response DTO
        long count = employeeRepository.countByDepartmentId(savedDept.getId());
        return mapToResponse(savedDept, count);
    }

    public List<DepartmentManagerHistoryResponseDTO> getManagerHistory(Long departmentId) {
        // Step 1: Validate Department ID
        if (!departmentRepository.existsById(departmentId)) {
            throw new DepartmentNotFoundException("Department not found with ID: " + departmentId);
        }

        // Step 2: Fetch history logs
        List<DepartmentManagerHistory> histories = departmentManagerHistoryRepository.findByDepartmentIdOrderByChangedAtDesc(departmentId);

        // Step 3: Map to Response DTO list & Return
        List<DepartmentManagerHistoryResponseDTO> dtoList = new ArrayList<>();
        for (DepartmentManagerHistory h : histories) {
            dtoList.add(new DepartmentManagerHistoryResponseDTO(
                    h.getId(),
                    h.getDepartment().getId(),
                    h.getPreviousManagerName(),
                    h.getNewManagerName(),
                    h.getChangedAt()
            ));
        }
        return dtoList;
    }

    private String derivePrefix(String name) {
        String clean = name.trim().replaceAll("[^a-zA-Z\\s]", "").toUpperCase();
        String[] words = clean.split("\\s+");
        StringBuilder prefix = new StringBuilder();
        if (words.length > 1) {
            for (String word : words) {
                if (!word.isEmpty()) {
                    prefix.append(word.charAt(0));
                }
            }
        } else if (words.length == 1 && !words[0].isEmpty()) {
            String word = words[0];
            if (word.length() >= 3) {
                prefix.append(word.substring(0, 3));
            } else {
                prefix.append(word);
            }
        } else {
            prefix.append("DEPT");
        }
        return prefix.length() > 4 ? prefix.substring(0, 4) : prefix.toString();
    }

    private String generateDepartmentCode(String prefix) {
        Optional<Department> latest = departmentRepository.findFirstByDepartmentCodeStartingWithOrderByIdDesc(prefix);
        int nextNum = 1;
        if (latest.isPresent()) {
            String lastCode = latest.get().getDepartmentCode();
            String numStr = lastCode.substring(prefix.length());
            try {
                nextNum = Integer.parseInt(numStr) + 1;
            } catch (NumberFormatException e) {
                nextNum = 1;
            }
        }
        return String.format("%s%04d", prefix, nextNum);
    }

    private DepartmentResponseDTO mapToResponse(Department dept, long count) {
        return new DepartmentResponseDTO(
                dept.getId(),
                dept.getDepartmentCode(),
                dept.getDepartmentName(),
                dept.getDescription(),
                dept.getDepartmentHead(),
                dept.getStatus(),
                count
        );
    }

    public ResponseEntity<List<DepartmentNameResponseDTO>> getAllDepartmentName() {
        // Step 1: Fetch All Departments
        List<Department> departments = departmentRepository.findAll();
        List<DepartmentNameResponseDTO> response = new ArrayList<>();

        // Step 2: Map to response objects
        for (Department dept : departments) {
            DepartmentNameResponseDTO res = new DepartmentNameResponseDTO();
            res.setId(dept.getId());
            res.setName(dept.getDepartmentName());
            response.add(res);
        }

        // Step 3: Return Response
        return ResponseEntity.ok(response);
    }
}
