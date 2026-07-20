package com.example.ems.repository;

import com.example.ems.entity.DepartmentManagerHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentManagerHistoryRepository extends JpaRepository<DepartmentManagerHistory, Long> {
    List<DepartmentManagerHistory> findByDepartmentIdOrderByChangedAtDesc(Long departmentId);
}
