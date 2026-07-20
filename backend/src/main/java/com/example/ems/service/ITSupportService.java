package com.example.ems.service;

import com.example.ems.dto.response.EmployeeResponseDTO;
import com.example.ems.entity.Employee;
import com.example.ems.enums.EmploymentStatus;
import com.example.ems.enums.Role;
import com.example.ems.exception.BadRequestException;
import com.example.ems.exception.EmployeeNotFoundException;
import com.example.ems.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;

@Service
public class ITSupportService {

    private static final Logger log = LoggerFactory.getLogger(ITSupportService.class);

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public ITSupportService(EmployeeRepository employeeRepository,
                            PasswordEncoder passwordEncoder,
                            NotificationService notificationService) {
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Transactional
    public EmployeeResponseDTO resetPassword(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (employee.getRole() == Role.HR) {
            throw new BadRequestException("IT Support cannot modify HR accounts.");
        }

        String plainPassword = generateRandomPassword();
        System.out.println("reset password : " + plainPassword);
        employee.setPassword(passwordEncoder.encode(plainPassword));
        employee.setFirstLogin(true);

        Employee saved = employeeRepository.save(employee);
        log.info("Password reset executed by IT Support for employee: {}", saved.getEmployeeCode());

        notificationService.sendPasswordResetEmail(saved.getEmail(), saved.getFirstName(), plainPassword);

        return mapToResponse(saved);
    }

    @Transactional
    public EmployeeResponseDTO unlockAccount(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (employee.getRole() == Role.HR) {
            throw new BadRequestException("IT Support cannot modify HR accounts.");
        }

        employee.setAccountLocked(false);
        Employee saved = employeeRepository.save(employee);
        log.info("Account UNLOCKED by IT Support for employee: {}", saved.getEmployeeCode());

        return mapToResponse(saved);
    }

    @Transactional
    public EmployeeResponseDTO enableAccount(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (employee.getRole() == Role.HR) {
            throw new BadRequestException("IT Support cannot modify HR accounts.");
        }

        employee.setAccountEnabled(true);
        if (employee.getEmploymentStatus() == EmploymentStatus.SUSPENDED) {
            employee.setEmploymentStatus(EmploymentStatus.ACTIVE);
        }
        Employee saved = employeeRepository.save(employee);
        log.info("Account ENABLED by IT Support for employee: {}", saved.getEmployeeCode());

        return mapToResponse(saved);
    }

    @Transactional
    public EmployeeResponseDTO disableAccount(Long employeeId) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with ID: " + employeeId));

        if (employee.getRole() == Role.HR) {
            throw new BadRequestException("IT Support cannot modify HR accounts.");
        }

        if (employee.getRole() == Role.MANAGER) {
            List<Employee> reports = employeeRepository.findByManagerId(employeeId);
            boolean hasActiveReports = false;
            for (Employee r : reports) {
                if (r.getEmploymentStatus() == EmploymentStatus.ACTIVE) {
                    hasActiveReports = true;
                    break;
                }
            }
            if (hasActiveReports) {
                throw new BadRequestException("Cannot suspend manager because they have active reporting employees. Please reassign the employees first.");
            }
        }

        employee.setAccountEnabled(false);
        employee.setEmploymentStatus(EmploymentStatus.SUSPENDED);
        Employee saved = employeeRepository.save(employee);
        log.info("Account DISABLED by IT Support for employee: {}", saved.getEmployeeCode());

        return mapToResponse(saved);
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

        int length = 8 + random.nextInt(5); // 8 to 12
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
}
