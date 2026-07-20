package com.example.ems.controller;

import com.example.ems.dto.request.ChangePasswordRequestDTO;
import com.example.ems.dto.request.LoginRequestDTO;
import com.example.ems.dto.response.LoginResponseDTO;
import com.example.ems.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/bootstrap")
    public ResponseEntity<LoginResponseDTO> bootstrapHR(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authenticationService.bootstrapHR(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        LoginResponseDTO response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequestDTO request) {
        authenticationService.changePassword(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }

}
