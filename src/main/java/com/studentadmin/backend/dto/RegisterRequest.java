package com.studentadmin.backend.dto;

import com.studentadmin.backend.model.Role;
import lombok.Data;

@Data
public class RegisterRequest {
    private String username;
    private String password;
    private Role role; // Optional, defaults to STUDENT if null in logic, but Admin might specify
}
