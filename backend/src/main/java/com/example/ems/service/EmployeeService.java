package com.example.ems.service;

import com.example.ems.dto.request.EmployeeRequestDTO;
import com.example.ems.dto.request.EmployeeSelfUpdateDTO;
import com.example.ems.dto.response.*;
import com.example.ems.entity.*;
import com.example.ems.enums.EmploymentStatus;
import com.example.ems.enums.Role;
import com.example.ems.exception.*;
import com.example.ems.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;

@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final PerformanceReviewRepository performanceReviewRepository;
    private final TaskRepository taskRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           AttendanceRepository attendanceRepository,
                           LeaveRequestRepository leaveRequestRepository,
                           PerformanceReviewRepository performanceReviewRepository,
                           TaskRepository taskRepository,
                           PasswordEncoder passwordEncoder,
                           NotificationService notificationService) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.leaveRequestRepository = leaveRequestRepository;
        this.performanceReviewRepository = performanceReviewRepository;
        this.taskRepository = taskRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Transactional
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO dto) {

        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + dto.getEmail());
        }
        if (employeeRepository.existsByPhone(dto.getPhone())) {
            throw new DuplicatePhoneException("Phone number already exists: " + dto.getPhone());
        }

        Department department = departmentRepository.findById(dto.getDepartmentId())
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with ID: " + dto.getDepartmentId()));

        Employee manager = null;
        if (dto.getRole() == Role.HR) {
            if (dto.getManagerId() != null) {
                throw new BadRequestException("HR accounts must not have a reporting manager");
            }
        } else if (dto.getRole() == Role.MANAGER) {
            List<Employee> deptManagers = employeeRepository.findByDepartmentIdAndRole(dto.getDepartmentId(), Role.MANAGER);
            if (!deptManagers.isEmpty()) {
                throw new BadRequestException("This department already has a manager. Only one manager is allowed per department.");
            }
            if (dto.getManagerId() != null) {
                manager = employeeRepository.findById(dto.getManagerId())
                        .orElseThrow(() -> new EmployeeNotFoundException("Manager not found with ID: " + dto.getManagerId()));
                if (manager.getRole() != Role.HR) {
                    throw new BadRequestException("Manager must report to an HR account only");
                }
            } else {
                List<Employee> hrs = employeeRepository.findByRole(Role.HR);
                if (!hrs.isEmpty()) {
                    manager = hrs.get(0);
                } else {
                    throw new BadRequestException("No HR account found to assign as manager");
                }
            }
        } else if (dto.getRole() == Role.IT_SUPPORT) {
            List<Employee> hrs = employeeRepository.findByRole(Role.HR);
            if (!hrs.isEmpty()) {
                manager = hrs.get(0);
            } else {
                throw new BadRequestException("No HR account found to assign as reporting manager for IT Support.");
            }
            department = departmentRepository.findByDepartmentName("IT Support")
                    .orElse(department);
        } else if (dto.getRole() == Role.EMPLOYEE) {
            if (dto.getManagerId() != null) {
                manager = employeeRepository.findById(dto.getManagerId())
                        .orElseThrow(() -> new EmployeeNotFoundException("Manager not found with ID: " + dto.getManagerId()));
                if (manager.getRole() != Role.MANAGER) {
                    throw new BadRequestException("Employees must report to a MANAGER account only");
                }
                if (manager.getDepartment() == null || !manager.getDepartment().getId().equals(dto.getDepartmentId())) {
                    throw new BadRequestException("The reporting manager must belong to the selected department");
                }
            } else {
                throw new BadRequestException("Reporting manager is required");
            }
        }
        String code = generateEmployeeCode();
        String plainPassword = generateRandomPassword();
        System.out.println(dto.getFirstName() + " " + dto.getLastName() + " : " + plainPassword);

        Employee emp = new Employee();
        emp.setEmployeeCode(code);
        emp.setFirstName(dto.getFirstName());
        emp.setLastName(dto.getLastName());
        emp.setEmail(dto.getEmail());
        emp.setPhone(dto.getPhone());
        emp.setGender(dto.getGender());
        emp.setDateOfBirth(dto.getDateOfBirth());
        emp.setJoiningDate(dto.getJoiningDate());
        emp.setDesignation(dto.getDesignation());
        emp.setSalary(dto.getSalary());
        emp.setAddress(dto.getAddress());
        emp.setEmploymentStatus(dto.getEmploymentStatus());
        emp.setPassword(passwordEncoder.encode(plainPassword));
        emp.setRole(dto.getRole());
        emp.setDepartment(department);
        emp.setManager(manager);
        emp.setFirstLogin(true);
        emp.setAccountEnabled(true);
        emp.setAccountLocked(false);

        Employee saved = employeeRepository.save(emp);
        log.info("Employee created successfully: Code={}, Name={} {}", saved.getEmployeeCode(), saved.getFirstName(), saved.getLastName());

        if (saved.getRole() == Role.MANAGER) {
            Department dept = saved.getDepartment();
            if (dept.getDepartmentHead() == null || dept.getDepartmentHead().trim().isEmpty() || dept.getDepartmentHead().equalsIgnoreCase("None") || dept.getDepartmentHead().equalsIgnoreCase("ADMIN")) {
                dept.setDepartmentHead(saved.getFirstName() + " " + saved.getLastName());
                departmentRepository.save(dept);
            }
        }

        notificationService.sendEmployeeAccountEmail(saved.getEmail(), saved.getFirstName(), saved.getEmployeeCode(), plainPassword);

        return mapToResponse(saved);
    }

    public List<EmployeeResponseDTO> getAllEmployees() {

        Employee currentUser = getCurrentUser();
        List<Employee> list;

        if (currentUser.getRole() == Role.HR || currentUser.getRole() == Role.IT_SUPPORT) {
            list = employeeRepository.findAll();
        } else if (currentUser.getRole() == Role.MANAGER) {
            list = employeeRepository.findByManagerId(currentUser.getId());
        } else {
            list = Collections.singletonList(currentUser);
        }

        List<EmployeeResponseDTO> dtoList = new ArrayList<>();
        for (Employee emp : list) {
            dtoList.add(mapToResponse(emp));
        }
        return dtoList;
    }

    public EmployeeResponseDTO getEmployeeById(Long id) {

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));

        Employee currentUser = getCurrentUser();
        verifyAccessScope(currentUser, emp);

        return mapToResponse(emp);
    }

    @Transactional
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO dto) {

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));

        Employee currentUser = getCurrentUser();
        verifyAccessScope(currentUser, emp);

        if (currentUser.getRole() == Role.HR) {

            Optional<Employee> optEmail = employeeRepository.findByEmail(dto.getEmail());
            if (optEmail.isPresent() && !optEmail.get().getId().equals(id)) {
                throw new DuplicateEmailException("Email already exists: " + dto.getEmail());
            }

            Optional<Employee> optPhone = employeeRepository.findByPhone(dto.getPhone());
            if (optPhone.isPresent() && !optPhone.get().getId().equals(id)) {
                throw new DuplicatePhoneException("Phone number already exists: " + dto.getPhone());
            }


            if (emp.getRole() == Role.MANAGER && dto.getEmploymentStatus() != EmploymentStatus.ACTIVE) {
                List<Employee> reports = employeeRepository.findByManagerId(id);
                boolean hasActiveReports = false;
                for (Employee r : reports) {
                    if (r.getEmploymentStatus() == EmploymentStatus.ACTIVE) {
                        hasActiveReports = true;
                        break;
                    }
                }
                if (hasActiveReports) {
                    throw new BadRequestException("Cannot terminate/resign manager because they have active reporting employees. Please reassign the employees first.");
                }
            }

            Department dept = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not found with ID: " + dto.getDepartmentId()));

            Employee manager = null;
            if (dto.getRole() == Role.HR) {
                if (dto.getManagerId() != null) {
                    throw new BadRequestException("HR accounts must not have a reporting manager");
                }
            } else if (dto.getRole() == Role.MANAGER) {
                List<Employee> deptManagers = employeeRepository.findByDepartmentIdAndRole(dto.getDepartmentId(), Role.MANAGER);
                boolean alreadyHasManager = false;
                for (Employee m : deptManagers) {
                    if (!m.getId().equals(id)) {
                        alreadyHasManager = true;
                        break;
                    }
                }
                if (alreadyHasManager) {
                    throw new BadRequestException("This department already has a manager. Only one manager is allowed per department.");
                }
                if (dto.getManagerId() != null) {
                    manager = employeeRepository.findById(dto.getManagerId())
                            .orElseThrow(() -> new EmployeeNotFoundException("Manager not found with ID: " + dto.getManagerId()));
                    if (manager.getRole() != Role.HR) {
                        throw new BadRequestException("Manager must report to an HR account only");
                    }
                } else {
                    List<Employee> hrs = employeeRepository.findByRole(Role.HR);
                    if (!hrs.isEmpty()) {
                        manager = hrs.get(0);
                    } else {
                        throw new BadRequestException("No HR account found to assign as manager");
                    }
                }
            } else if (dto.getRole() == Role.IT_SUPPORT) {
                List<Employee> hrs = employeeRepository.findByRole(Role.HR);
                if (!hrs.isEmpty()) {
                    manager = hrs.get(0);
                } else {
                    throw new BadRequestException("No HR account found to assign as reporting manager for IT Support.");
                }
                dept = departmentRepository.findByDepartmentName("IT Support")
                        .orElse(dept);
            } else if (dto.getRole() == Role.EMPLOYEE) {
                if (dto.getManagerId() != null) {
                    manager = employeeRepository.findById(dto.getManagerId())
                            .orElseThrow(() -> new EmployeeNotFoundException("Manager not found with ID: " + dto.getManagerId()));
                    if (manager.getRole() != Role.MANAGER) {
                        throw new BadRequestException("Employees must report to a MANAGER account only");
                    }
                    if (manager.getDepartment() == null || !manager.getDepartment().getId().equals(dto.getDepartmentId())) {
                        throw new BadRequestException("The reporting manager must belong to the selected department");
                    }
                } else {
                    throw new BadRequestException("Reporting manager is required");
                }
            }

            emp.setFirstName(dto.getFirstName());
            emp.setLastName(dto.getLastName());
            emp.setEmail(dto.getEmail());
            emp.setPhone(dto.getPhone());
            emp.setGender(dto.getGender());
            emp.setDateOfBirth(dto.getDateOfBirth());
            emp.setJoiningDate(dto.getJoiningDate());
            emp.setDesignation(dto.getDesignation());
            emp.setSalary(dto.getSalary());
            emp.setAddress(dto.getAddress());
            emp.setEmploymentStatus(dto.getEmploymentStatus());
            emp.setRole(dto.getRole());
            emp.setDepartment(dept);
            emp.setManager(manager);

        } else if (currentUser.getRole() == Role.MANAGER) {
            Employee manager = null;
            if (dto.getManagerId() != null) {
                manager = employeeRepository.findById(dto.getManagerId())
                        .orElseThrow(() -> new EmployeeNotFoundException("Manager not found with ID: " + dto.getManagerId()));
                if (manager.getRole() != Role.MANAGER) {
                    throw new BadRequestException("Employees must report to a MANAGER account only");
                }
            }

            emp.setDesignation(dto.getDesignation());
            emp.setEmploymentStatus(dto.getEmploymentStatus());
            emp.setManager(manager);
        } else {
            throw new AccessDeniedException("Employees cannot update profiles via HR endpoint");
        }

        Employee saved = employeeRepository.save(emp);

        return mapToResponse(saved);
    }

    @Transactional
    public void deleteEmployee(Long id) {

        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));

        Employee currentUser = getCurrentUser();
        if (currentUser.getId().equals(id)) {
            throw new BadRequestException("HR must not be allowed to delete their own profile");
        }
        if (emp.getRole() == Role.HR) {
            throw new BadRequestException("HR accounts must never be deleted.");
        }
        if (emp.getRole() == Role.MANAGER) {
            throw new BadRequestException("Managers must never be physically deleted. Use status updates instead.");
        }

        if (!attendanceRepository.findByEmployeeOrderByAttendanceDateDesc(emp).isEmpty()) {
            throw new BadRequestException("Cannot delete employee: linked attendance records exist.");
        }
        if (!leaveRequestRepository.findByEmployeeOrderByStartDateDesc(emp).isEmpty()) {
            throw new BadRequestException("Cannot delete employee: linked leave requests exist.");
        }
        if (!performanceReviewRepository.findByEmployeeOrderByReviewDateDesc(emp).isEmpty()) {
            throw new BadRequestException("Cannot delete employee: linked performance reviews exist.");
        }
        if (!taskRepository.findByAssigneeOrderByIdDesc(emp).isEmpty()) {
            throw new BadRequestException("Cannot delete employee: linked assigned tasks exist.");
        }

        employeeRepository.delete(emp);
        log.info("Employee deleted physically: ID={}, Code={}", id, emp.getEmployeeCode());
    }

    public EmployeeResponseDTO getProfile() {
        Employee currentUser = getCurrentUser();

        return mapToResponse(currentUser);
    }

    @Transactional
    public EmployeeResponseDTO selfUpdate(EmployeeSelfUpdateDTO dto) {

        Employee emp = getCurrentUser();

        Optional<Employee> optEmail = employeeRepository.findByEmail(dto.getEmail());
        if (optEmail.isPresent() && !optEmail.get().getId().equals(emp.getId())) {
            throw new DuplicateEmailException("Email already exists: " + dto.getEmail());
        }

        Optional<Employee> optPhone = employeeRepository.findByPhone(dto.getPhone());
        if (optPhone.isPresent() && !optPhone.get().getId().equals(emp.getId())) {
            throw new DuplicatePhoneException("Phone number already exists: " + dto.getPhone());
        }

        emp.setEmail(dto.getEmail());
        emp.setPhone(dto.getPhone());
        emp.setAddress(dto.getAddress());

        Employee saved = employeeRepository.save(emp);

        return mapToResponse(saved);
    }

    public List<EmployeeResponseDTO> searchEmployees(String code, String email, String department, String role) {

        Employee currentUser = getCurrentUser();
        List<Employee> results = new ArrayList<>();

        if (code != null) {
            employeeRepository.findByEmployeeCode(code).ifPresent(results::add);
        } else if (email != null) {
            employeeRepository.findByEmail(email).ifPresent(results::add);
        } else if (department != null) {
            results.addAll(employeeRepository.findByDepartment_DepartmentName(department));
        } else if (role != null) {
            try {
                results.addAll(employeeRepository.findByRole(Role.valueOf(role.toUpperCase())));
            } catch (IllegalArgumentException e) {
               //
            }
        } else {
            results.addAll(employeeRepository.findAll());
        }


        List<EmployeeResponseDTO> filteredList = new ArrayList<>();
        for (Employee emp : results) {
            try {
                verifyAccessScope(currentUser, emp);
                filteredList.add(mapToResponse(emp));
            } catch (AccessDeniedException e) {
                //
            }
        }

        return filteredList;
    }

    @Transactional(readOnly = true)
    public EmployeeProfileDetailsDTO getEmployeeProfileDetails(Long id) {

        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + id));

        Employee currentUser = getCurrentUser();
        verifyAccessScope(currentUser, employee);

        EmployeeResponseDTO basicDetails = mapToResponse(employee);

        List<PerformanceReview> reviews = performanceReviewRepository.findByEmployeeOrderByReviewDateDesc(employee);
        List<PerformanceResponseDTO> performanceSummary = new ArrayList<>();
        for (PerformanceReview pr : reviews) {
            String empName = pr.getEmployee().getFirstName() + " " + pr.getEmployee().getLastName();
            String deptName = pr.getEmployee().getDepartment() != null ? pr.getEmployee().getDepartment().getDepartmentName() : "None";
            performanceSummary.add(new PerformanceResponseDTO(
                    pr.getId(),
                    pr.getEmployee().getId(),
                    pr.getEmployee().getEmployeeCode(),
                    empName,
                    pr.getReviewPeriod(),
                    pr.getPerformanceGoal(),
                    pr.getAchievement(),
                    pr.getStrength(),
                    pr.getImprovementArea(),
                    pr.getRating(),
                    pr.getFeedback(),
                    pr.getReviewDate(),
                    deptName
            ));
        }

        List<Attendance> attendances = attendanceRepository.findByEmployeeOrderByAttendanceDateDesc(employee);
        List<AttendanceResponseDTO> attendanceSummary = new ArrayList<>();
        for (Attendance a : attendances) {
            attendanceSummary.add(new AttendanceResponseDTO(
                    a.getAttendanceDate(),
                    a.getClockInTime(),
                    a.getClockOutTime(),
                    a.getWorkingHours(),
                    a.getOvertimeHours(),
                    a.getAttendanceStatus(),
                    a.getEmployee().getEmployeeCode(),
                    a.getEmployee().getFirstName() + " " + a.getEmployee().getLastName()
            ));
        }

        List<LeaveRequest> leaves = leaveRequestRepository.findByEmployeeOrderByStartDateDesc(employee);
        List<LeaveResponseDTO> leaveSummary = new ArrayList<>();
        for (LeaveRequest lr : leaves) {
            leaveSummary.add(new LeaveResponseDTO(
                    lr.getId(),
                    lr.getEmployee().getEmployeeCode(),
                    lr.getEmployee().getFirstName() + " " + lr.getEmployee().getLastName(),
                    lr.getLeaveType(),
                    lr.getStartDate(),
                    lr.getEndDate(),
                    lr.getTotalDays(),
                    lr.getReason(),
                    lr.getStatus(),
                    lr.getManagerComments(),
                    lr.getApprovedDate(),
                    lr.getEmployee().getRole().name()
            ));
        }

        return new EmployeeProfileDetailsDTO(basicDetails, performanceSummary, attendanceSummary, leaveSummary);
    }

    private void verifyAccessScope(Employee currentUser, Employee targetEmployee) {
        if (currentUser.getRole() == Role.HR || currentUser.getRole() == Role.IT_SUPPORT) {
            return;
        }
        if (currentUser.getRole() == Role.MANAGER) {
            if (targetEmployee.getManager() != null &&
                    targetEmployee.getManager().getEmployeeCode().equals(currentUser.getEmployeeCode())) {
                return;
            }
            if (targetEmployee.getId().equals(currentUser.getId())) {
                return;
            }
            throw new AccessDeniedException("Managers can only access their direct reports");
        }
        if (currentUser.getRole() == Role.EMPLOYEE) {
            if (targetEmployee.getId().equals(currentUser.getId())) {
                return;
            }
            throw new AccessDeniedException("Employees can only access their own records");
        }
        throw new AccessDeniedException("Access is denied for this account role");
    }

    private Employee getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        return employeeRepository.findByEmployeeCode(auth.getName())
                .orElseThrow(() -> new EmployeeNotFoundException("Authenticated employee not found"));
    }

    private String generateEmployeeCode() {
        Optional<Employee> latest = employeeRepository.findFirstByOrderByIdDesc();
        int nextNum = 1001;
        if (latest.isPresent()) {
            String lastCode = latest.get().getEmployeeCode();
            try {
                nextNum = Integer.parseInt(lastCode.substring(3)) + 1;
            } catch (NumberFormatException e) {
                nextNum = 1001;
            }
        }
        return String.format("GTS%04d", nextNum);
    }

    private String generateRandomPassword() {
        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specials = "!@#$%^&*()_+-=[]{}|;':,./<>?";
        SecureRandom random = new SecureRandom();

        char charUpper = upper.charAt(random.nextInt(upper.length()));
        char charLower = lower.charAt(random.nextInt(lower.length()));
        char charNum = numbers.charAt(random.nextInt(numbers.length()));
        char charSpec = specials.charAt(random.nextInt(specials.length()));

        String allChars = upper + lower + numbers + specials;
        StringBuilder sb = new StringBuilder();
        sb.append(charUpper).append(charLower).append(charNum).append(charSpec);

        int length = 8 + random.nextInt(5);
        for (int i = 4; i < length; i++) {
            sb.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    private EmployeeResponseDTO mapToResponse(Employee emp) {
        String deptName = emp.getDepartment() != null ? emp.getDepartment().getDepartmentName() : "None";
        String managerName = emp.getManager() != null ? emp.getManager().getFirstName() + " " + emp.getManager().getLastName() : "None";

        return new EmployeeResponseDTO(
                emp.getId(),
                emp.getEmployeeCode(),
                emp.getFirstName(),
                emp.getLastName(),
                emp.getEmail(),
                emp.getPhone(),
                emp.getGender(),
                emp.getDateOfBirth(),
                emp.getJoiningDate(),
                emp.getDesignation(),
                emp.getSalary(),
                emp.getAddress(),
                emp.getEmploymentStatus(),
                emp.getRole(),
                deptName,
                managerName,
                emp.getAccountEnabled(),
                emp.getAccountLocked(),
                emp.getFirstLogin(),
                emp.getCreatedAt(),
                emp.getUpdatedAt()
        );
    }

    public ResponseEntity<List<ManagerNameResponseDTO>> getManger() {

        List<Employee> emp = employeeRepository.findAllByRole(Role.MANAGER);
        List<ManagerNameResponseDTO> response = new ArrayList<>();

        for (Employee employee : emp) {
            ManagerNameResponseDTO res = new ManagerNameResponseDTO();
            res.setId(employee.getId());
            res.setManagerName(employee.getFirstName() + " " + employee.getLastName());
            response.add(res);
        }

        return ResponseEntity.ok(response);
    }
}
