package com.example.ems.repository;

import com.example.ems.entity.Employee;
import com.example.ems.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    Optional<Employee> findByEmployeeCode(String employeeCode);
    Optional<Employee> findFirstByOrderByIdDesc();
    Optional<Employee> findByEmail(String email);
    Optional<Employee> findByPhone(String phone);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    List<Employee> findByDepartment_DepartmentName(String departmentName);
    List<Employee> findByRole(Role role);
    long countByDepartmentId(Long departmentId);
    List<Employee> findByManagerId(Long managerId);
    List<Employee> findAllByRole(Role manager);
    List<Employee> findByDepartmentIdAndRole(Long departmentId, Role role);
}
