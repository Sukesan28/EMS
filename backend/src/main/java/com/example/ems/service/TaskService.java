package com.example.ems.service;

import com.example.ems.dto.request.TaskRequestDTO;
import com.example.ems.dto.response.TaskResponseDTO;
import com.example.ems.entity.Employee;
import com.example.ems.entity.Task;
import com.example.ems.enums.Role;
import com.example.ems.exception.BadRequestException;
import com.example.ems.exception.EmployeeNotFoundException;
import com.example.ems.repository.EmployeeRepository;
import com.example.ems.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private static final Logger log = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final EmployeeRepository employeeRepository;

    public TaskService(TaskRepository taskRepository, EmployeeRepository employeeRepository) {
        this.taskRepository = taskRepository;
        this.employeeRepository = employeeRepository;
    }

    @Transactional
    public TaskResponseDTO assignTask(TaskRequestDTO dto) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        Employee assigner = employeeRepository.findByEmployeeCode(auth.getName())
                .orElseThrow(() -> new EmployeeNotFoundException("Authenticated employee not found"));

        if (assigner.getRole() != Role.MANAGER) {
            throw new AccessDeniedException("Access denied. Only Managers can assign tasks.");
        }
        Employee assignee = employeeRepository.findById(dto.getAssigneeId())
                .orElseThrow(() -> new EmployeeNotFoundException("Assignee employee not found with ID: " + dto.getAssigneeId()));

        if (assignee.getManager() == null || !assignee.getManager().getId().equals(assigner.getId())) {
            throw new AccessDeniedException("Access denied. Managers can only assign tasks to their team members.");
        }

        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setAssignee(assignee);
        task.setAssigner(assigner);
        if (dto.getStatus() != null) {
            task.setStatus(dto.getStatus());
        } else {
            task.setStatus(com.example.ems.enums.TaskStatus.NOT_STARTED);
        }
        task.setDueDate(dto.getDueDate());

        Task saved = taskRepository.save(task);
        log.info("Task assigned successfully. Assigner: {}, Assignee: {}, Task ID: {}", 
                assigner.getEmployeeCode(), assignee.getEmployeeCode(), saved.getId());

        return mapToResponse(saved);
    }

    public List<TaskResponseDTO> getMyTasks() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        Employee employee = employeeRepository.findByEmployeeCode(auth.getName())
                .orElseThrow(() -> new EmployeeNotFoundException("Authenticated employee not found"));

        List<Task> tasks = taskRepository.findByAssigneeOrderByIdDesc(employee);

        List<TaskResponseDTO> responseList = new ArrayList<>();
        for (Task t : tasks) {
            responseList.add(mapToResponse(t));
        }
        return responseList;
    }

    public List<TaskResponseDTO> getTeamTasks() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BadRequestException("User not authenticated");
        }
        Employee currentUser = employeeRepository.findByEmployeeCode(auth.getName())
                .orElseThrow(() -> new EmployeeNotFoundException("Authenticated employee not found"));

        List<Task> tasks;
        if (currentUser.getRole() == Role.HR) {
            tasks = taskRepository.findAll();
        } else if (currentUser.getRole() == Role.MANAGER) {
            tasks = taskRepository.findByAssigneeManagerOrderByIdDesc(currentUser);
        } else {
            throw new AccessDeniedException("Access denied. Only HR and MANAGERS can view team tasks.");
        }

        List<TaskResponseDTO> responseList = new ArrayList<>();
        for (Task t : tasks) {
            responseList.add(mapToResponse(t));
        }
        return responseList;
    }

    private TaskResponseDTO mapToResponse(Task task) {
        String assigneeName = task.getAssignee().getFirstName() + " " + task.getAssignee().getLastName();
        String assignerName = task.getAssigner().getFirstName() + " " + task.getAssigner().getLastName();
        String deptName = task.getAssignee().getDepartment() != null ? task.getAssignee().getDepartment().getDepartmentName() : "None";
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getAssignee().getId(),
                assigneeName,
                task.getAssignee().getEmployeeCode(),
                deptName,
                task.getAssigner().getId(),
                assignerName,
                task.getCreatedAt(),
                task.getUpdatedAt(),
                task.getStatus() != null ? task.getStatus() : com.example.ems.enums.TaskStatus.NOT_STARTED,
                task.getDueDate()
        );
    }
}
