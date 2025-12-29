package com.studentadmin.backend.controller;

import com.studentadmin.backend.dto.RegisterRequest;
import com.studentadmin.backend.model.Role;
import com.studentadmin.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;

    public AdminController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/create-student")
    public ResponseEntity<?> createStudent(@RequestBody RegisterRequest request) {
        request.setRole(Role.STUDENT);
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody RegisterRequest request) {
        request.setRole(Role.ADMIN);
        return ResponseEntity.ok(authService.register(request));
    }
}
