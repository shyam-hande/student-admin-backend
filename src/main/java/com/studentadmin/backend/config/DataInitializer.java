package com.studentadmin.backend.config;

import com.studentadmin.backend.dto.RegisterRequest;
import com.studentadmin.backend.model.Role;
import com.studentadmin.backend.service.AuthService;
import com.studentadmin.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(AuthService authService, UserRepository userRepository) {
        return args -> {
            if (!userRepository.existsByUsername("admin")) {
                RegisterRequest admin = new RegisterRequest();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setRole(Role.ADMIN);
                authService.register(admin);
                System.out.println("Default Admin created: username=admin, password=admin123");
            }
        };
    }
}
