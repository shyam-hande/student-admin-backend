package com.studentadmin.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.studentadmin.backend.dto.AuthRequest;
import com.studentadmin.backend.dto.RegisterRequest;
import com.studentadmin.backend.model.Role;
import com.studentadmin.backend.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional // To roll back DB changes after each test
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        // Prepare a user for login tests
        // We use the real service to register, ensuring the DB state is valid for the
        // test
        try {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername("integrationUser");
            registerRequest.setPassword("password123");
            registerRequest.setRole(Role.STUDENT);
            authService.register(registerRequest);
        } catch (Exception e) {
            // Include catch block if user already exists from previous runs (though
            // @Transactional should handle cleanup)
        }
    }

    @Test
    void testLogin_Success() throws Exception {
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername("integrationUser");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists()) // Standard check
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    void testLogin_Failure_WrongPassword() throws Exception {
        AuthRequest loginRequest = new AuthRequest();
        loginRequest.setUsername("integrationUser");
        loginRequest.setPassword("wrongPassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isForbidden()); // Security default for bad credentials is mostly 401/403
    }
}
