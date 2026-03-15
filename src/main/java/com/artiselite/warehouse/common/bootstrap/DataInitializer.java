package com.artiselite.warehouse.common.bootstrap;

import com.artiselite.warehouse.common.config.AppSeedProperties;
import com.artiselite.warehouse.role.entity.Role;
import com.artiselite.warehouse.role.repository.RoleRepository;
import com.artiselite.warehouse.user.entity.User;
import com.artiselite.warehouse.user.repository.UserRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public ApplicationRunner seedUsers(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AppSeedProperties seedProperties
    ) {
        return args -> {
            seedUserIfMissing(
                    roleRepository,
                    userRepository,
                    passwordEncoder,
                    seedProperties.getManagerEmail(),
                    seedProperties.getManagerPassword(),
                    seedProperties.getManagerFullName(),
                    "MANAGER"
            );
            seedUserIfMissing(
                    roleRepository,
                    userRepository,
                    passwordEncoder,
                    seedProperties.getOperatorEmail(),
                    seedProperties.getOperatorPassword(),
                    seedProperties.getOperatorFullName(),
                    "OPERATOR"
            );
        };
    }

    private void seedUserIfMissing(
            RoleRepository roleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            String email,
            String password,
            String fullName,
            String roleName
    ) {
        if (userRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        Role role = roleRepository.findByNameIgnoreCase(roleName)
                .orElseThrow(() -> new IllegalStateException("Required role not found: " + roleName));

        User user = new User();
        user.setEmail(email.toLowerCase());
        user.setFullName(fullName);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setIsActive(true);
        userRepository.save(user);
    }
}
