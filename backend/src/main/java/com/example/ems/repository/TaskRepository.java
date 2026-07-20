package com.example.ems.repository;

import com.example.ems.entity.Employee;
import com.example.ems.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssigneeOrderByIdDesc(Employee assignee);
    List<Task> findByAssigneeManagerOrderByIdDesc(Employee manager);
}
