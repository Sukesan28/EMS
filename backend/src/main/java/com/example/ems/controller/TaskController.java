package com.example.ems.controller;

import com.example.ems.dto.request.TaskRequestDTO;
import com.example.ems.dto.response.TaskResponseDTO;
import com.example.ems.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TaskResponseDTO> assignTask(@Valid @RequestBody TaskRequestDTO dto) {
        TaskResponseDTO response = taskService.assignTask(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT')")
    public ResponseEntity<List<TaskResponseDTO>> getMyTasks() {
        List<TaskResponseDTO> response = taskService.getMyTasks();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/team")
    @PreAuthorize("hasAnyRole('HR', 'MANAGER')")
    public ResponseEntity<List<TaskResponseDTO>> getTeamTasks() {
        List<TaskResponseDTO> response = taskService.getTeamTasks();
        return ResponseEntity.ok(response);
    }
}
