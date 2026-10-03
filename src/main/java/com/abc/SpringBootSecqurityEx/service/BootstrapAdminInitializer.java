package com.abc.SpringBootSecqurityEx.service;

import com.abc.SpringBootSecqurityEx.entity.User;
import com.abc.SpringBootSecqurityEx.enums.ERole;
import com.abc.SpringBootSecqurityEx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Component
public class BootstrapAdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String username;
    private final String email;
    private final String password;

    public BootstrapAdminInitializer(UserRepository userRepository,
                                     PasswordEncoder passwordEncoder,
                                     @Value("${app.bootstrap-admin.username:}") String username,
                                     @Value("${app.bootstrap-admin.email:}") String email,
                                     @Value("${app.bootstrap-admin.password:}") String password) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(String... args) {
        if (username.isBlank() && email.isBlank() && password.isBlank()) {
            return;
        }
        if (username.isBlank() || email.isBlank() || password.length() < 4) {
            throw new IllegalStateException("Set all bootstrap admin values and use a password of at least 12 characters");
        }
        if (userRepository.existsByUserName(username)) {
            return;
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("Bootstrap admin email is already assigned to another account");
        }

        User admin = new User();
        admin.setUserName(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(password));
        admin.setUserFirstName("Administrator");
        admin.setRoles(EnumSet.of(ERole.ROLE_ADMIN));
        userRepository.save(admin);
    }
}
