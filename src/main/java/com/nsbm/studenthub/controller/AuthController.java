package com.nsbm.studenthub.controller;

import com.nsbm.studenthub.entity.User;
import com.nsbm.studenthub.repository.RoleRepository;
import com.nsbm.studenthub.repository.UserRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PasswordEncoder encoder;

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest req) {

        if (userRepo.findByEmail(req.getEmail()).isPresent()) {
            return "Email already exists!";
        }

        var roleUser = roleRepo.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("ROLE_USER not found"));

        User u = new User();
        u.setEmail(req.getEmail());
        u.setPassword(encoder.encode(req.getPassword()));
        u.setRoles(Set.of(roleUser));

        userRepo.save(u);
        return "User Registered";
    }

    // Test login with Basic Auth
    @GetMapping("/me")
    public Object me(Authentication auth) {
        return auth.getName() + " | " + auth.getAuthorities();
    }

    @Data
    public static class RegisterRequest {
        private String email;
        private String password;
    }
}
