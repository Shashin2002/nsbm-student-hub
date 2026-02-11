package com.nsbm.studenthub.config;

import com.nsbm.studenthub.entity.Role;
import com.nsbm.studenthub.entity.User;
import com.nsbm.studenthub.repository.RoleRepository;
import com.nsbm.studenthub.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {

        // Insert roles if missing
        Role adminRole = roleRepo.findByName("ROLE_ADMIN")
                .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_ADMIN")));

        Role userRole = roleRepo.findByName("ROLE_USER")
                .orElseGet(() -> roleRepo.save(new Role(null, "ROLE_USER")));

        // Create default admin user (only if not exists)
        userRepo.findByEmail("admin@nsbm.com").orElseGet(() -> {
            User admin = new User();
            admin.setEmail("admin@nsbm.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRoles(Set.of(adminRole, userRole));
            return userRepo.save(admin);
        });
    }
}
