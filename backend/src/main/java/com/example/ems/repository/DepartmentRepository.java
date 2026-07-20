package com.example.ems.repository;

import com.example.ems.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Optional<Department> findByDepartmentCode(String departmentCode);
    Optional<Department> findByDepartmentName(String departmentName);
    boolean existsByDepartmentName(String departmentName);
    Optional<Department> findFirstByDepartmentCodeStartingWithOrderByIdDesc(String prefix);
}
