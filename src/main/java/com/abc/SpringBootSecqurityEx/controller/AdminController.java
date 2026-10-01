package com.abc.SpringBootSecqurityEx.controller;
import com.abc.SpringBootSecqurityEx.dtos.AdminStatistics;
import com.abc.SpringBootSecqurityEx.dtos.RoleUpdateRequest;
import com.abc.SpringBootSecqurityEx.dtos.UserDTO;
import com.abc.SpringBootSecqurityEx.entity.User;
import com.abc.SpringBootSecqurityEx.enums.ERole;
import com.abc.SpringBootSecqurityEx.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.EnumSet;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ADMIN')")  // Fixed: changed USER to ADMIN
@PreAuthorize("hasAuthority('ROLE_ADMIN')")  // Use hasAuthority with full name
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    // List the fixed roles supported by the application.
    @GetMapping("/roles")
    public ResponseEntity<List<ERole>> getAllRoles() {
        return ResponseEntity.ok(List.of(ERole.values()));
    }

    // UPDATE USER ROLES
    @PutMapping("/users/{username}/roles")
    public ResponseEntity<UserDTO> updateUserRoles(
            @PathVariable String username,
            @Valid @RequestBody RoleUpdateRequest roleUpdateRequest) {

        User user = userRepository.findByUserName(username).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        EnumSet<ERole> roles = EnumSet.noneOf(ERole.class);
        roles.addAll(roleUpdateRequest.getRoles());
        user.setRoles(roles);
        User updatedUser = userRepository.save(user);

        return ResponseEntity.ok(toDto(updatedUser));
    }

    // GET USERS BY ROLE
    @GetMapping("/roles/{roleName}/users")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String roleName) {
        ERole role;
        try {
            role = ERole.valueOf(roleName);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }

        List<UserDTO> users = userRepository.findAll().stream()
                .filter(user -> user.getRoles().contains(role))
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(users);
    }

    // System statistics are restricted to administrators.
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdminStatistics> getSystemStatistics() {
        long totalUsers = userRepository.count();
        long enabledUsers = userRepository.findAll().stream()
                .filter(User::getEnabled)
                .count();
        AdminStatistics stats = new AdminStatistics(totalUsers, enabledUsers, List.of(ERole.values()));
        return ResponseEntity.ok(stats);
    }

    private UserDTO toDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserName(user.getUserName());
        dto.setUserFirstName(user.getUserFirstName());
        dto.setUserLastName(user.getUserLastName());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.getEnabled());
        dto.setRoles(user.getRoles());
        return dto;
    }
}

