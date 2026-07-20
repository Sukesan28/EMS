package com.example.ems.service;

import com.example.ems.dto.request.ChangePasswordRequestDTO;
import com.example.ems.dto.request.LoginRequestDTO;
import com.example.ems.dto.response.LoginResponseDTO;
import com.example.ems.entity.Department;
import com.example.ems.entity.Employee;
import com.example.ems.enums.DepartmentStatus;
import com.example.ems.enums.EmploymentStatus;
import com.example.ems.enums.Gender;
import com.example.ems.enums.Role;
import com.example.ems.exception.BadRequestException;
import com.example.ems.repository.DepartmentRepository;
import com.example.ems.repository.EmployeeRepository;
import com.example.ems.security.JwtService;
import com.example.ems.security.CustomUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class AuthenticationService {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public AuthenticationService(EmployeeRepository employeeRepository,
                                 DepartmentRepository departmentRepository,
                                 PasswordEncoder passwordEncoder,
                                 AuthenticationManager authenticationManager,
                                 JwtService jwtService,
                                 CustomUserDetailsService userDetailsService) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Transactional
    public LoginResponseDTO bootstrapHR(LoginRequestDTO request) {
        if (employeeRepository.findByEmployeeCode("GTS1001").isPresent()) {
            log.warn("Initialization request denied: GTS1001 administrator account already exists");
            throw new BadRequestException("System initialization is only allowed when GTS1001 administrator account does not exist");
        }

        Department hrDept = departmentRepository.findByDepartmentCode("HR0001")
                .orElseGet(() -> {
                    Department newDept = new Department();
                    newDept.setDepartmentCode("HR0001");
                    newDept.setDepartmentName("HR");
                    newDept.setDescription("Primary HR Department");
                    newDept.setDepartmentHead("ADMIN");
                    newDept.setStatus(DepartmentStatus.ACTIVE);
                    return departmentRepository.save(newDept);
                });

        Employee hrEmployee = new Employee();
        hrEmployee.setEmployeeCode("GTS1001");
        hrEmployee.setFirstName("Sukesan");
        hrEmployee.setLastName("Ramasamy");
        hrEmployee.setEmail("sukesanramasamy204@gmail.com");
        hrEmployee.setPhone("9876543210");
        hrEmployee.setGender(Gender.MALE);
        hrEmployee.setDateOfBirth(LocalDate.of(2005, 7, 15));
        hrEmployee.setJoiningDate(LocalDate.now());
        hrEmployee.setDesignation("Primary HR");
        hrEmployee.setSalary(50000.0);
        hrEmployee.setAddress("GlobalTech HQ");
        hrEmployee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        hrEmployee.setPassword(passwordEncoder.encode(request.getPassword()));
        hrEmployee.setRole(Role.HR);
        hrEmployee.setDepartment(hrDept);
        hrEmployee.setFirstLogin(true);
        hrEmployee.setAccountEnabled(true);
        hrEmployee.setAccountLocked(false);

        Employee saved = employeeRepository.save(hrEmployee);
        log.info("System successfully initialized with first HR Employee Code: GTS1001");

        UserDetails userDetails = userDetailsService.loadUserByUsername(saved.getEmployeeCode());
        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(
                saved.getEmployeeCode(),
                saved.getFirstName() + " " + saved.getLastName(),
                saved.getRole().name(),
                saved.getFirstLogin(),
                "HR Admin Account Created successfully",
                token
        );
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        Employee employee = employeeRepository.findByEmployeeCode(request.getEmployeeCode())
                .orElseGet(() -> {
                    log.warn("Login failed: Invalid Employee Code {}", request.getEmployeeCode());
                    throw new BadRequestException("Invalid Employee Code");
                });

        if (!passwordEncoder.matches(request.getPassword(), employee.getPassword())) {
            log.warn("Login failed: Invalid Password for employee code {}", request.getEmployeeCode());
            throw new BadRequestException("Invalid UserName and Password");
        }

        if (!employee.getAccountEnabled()) {
            log.warn("Login failed: Account Disabled for employee code {}", request.getEmployeeCode());
            throw new BadRequestException("Account Disabled");
        }

        if (employee.getAccountLocked()) {
            log.warn("Login failed: Account Locked for employee code {}", request.getEmployeeCode());
            throw new BadRequestException("Account Locked");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmployeeCode(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("Login successful for employee code: {}", request.getEmployeeCode());

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtService.generateToken(userDetails);

        return new LoginResponseDTO(
                employee.getEmployeeCode(),
                employee.getFirstName() + " " + employee.getLastName(),
                employee.getRole().name(),
                employee.getFirstLogin(),
                "Login Successful",
                token
        );
    }

    @Transactional
    public void changePassword(ChangePasswordRequestDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }

        String employeeCode = authentication.getName();
        Employee employee = employeeRepository.findByEmployeeCode(employeeCode)
                .orElseThrow(() -> new BadRequestException("Employee not found"));

        if (!passwordEncoder.matches(request.getOldPassword(), employee.getPassword())) {
            log.warn("Change password failed: Old password does not match for employee code: {}", employeeCode);
            throw new BadRequestException("Old password does not match");
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
        }

        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employee.setFirstLogin(false);
        employeeRepository.save(employee);
        log.info("Password changed successfully for employee code: {}", employeeCode);

        Authentication newAuth = new UsernamePasswordAuthenticationToken(
                authentication.getPrincipal(),
                employee.getPassword(),
                authentication.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }
}
