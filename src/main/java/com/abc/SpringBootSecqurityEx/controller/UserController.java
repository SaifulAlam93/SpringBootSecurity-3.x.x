package com.abc.SpringBootSecqurityEx.controller;
import com.abc.SpringBootSecqurityEx.dtos.StatusUpdateRequest;
import com.abc.SpringBootSecqurityEx.dtos.UserDTO;
import com.abc.SpringBootSecqurityEx.dtos.UserUpdateRequest;
import com.abc.SpringBootSecqurityEx.entity.User;
import com.abc.SpringBootSecqurityEx.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // GET ALL USERS - Only accessible by ADMIN
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream().map(this::toDto).toList());
    }

    // GET USER BY USERNAME - Accessible by ADMIN or the user themselves
    @GetMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        Optional<User> user = userRepository.findByUserName(username);
        return user.map(this::toDto).map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE USER - Accessible by ADMIN or the user themselves
    @PutMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN') or #username == authentication.principal.username")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable String username,
            @Valid @RequestBody UserUpdateRequest updateRequest) {

        Optional<User> existingUser = userRepository.findByUserName(username);
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = existingUser.get();

        // Only allow updating specific fields
        if (updateRequest.getFirstName() != null) {
            user.setUserFirstName(updateRequest.getFirstName());
        }
        if (updateRequest.getLastName() != null) {
            user.setUserLastName(updateRequest.getLastName());
        }
        if (updateRequest.getEmail() != null) {
            user.setEmail(updateRequest.getEmail());
        }

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(toDto(updatedUser));
    }

    // DELETE USER - Only accessible by ADMIN
    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        if (!userRepository.existsById(username)) {
            return ResponseEntity.notFound().build();
        }
        userRepository.deleteById(username);
        return ResponseEntity.ok("User deleted successfully");
    }

    // ENABLE/DISABLE USER - Only accessible by ADMIN
    @PatchMapping("/{username}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> updateUserStatus(
            @PathVariable String username,
            @Valid @RequestBody StatusUpdateRequest statusRequest) {

        Optional<User> existingUser = userRepository.findByUserName(username);
        if (existingUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = existingUser.get();
        user.setEnabled(statusRequest.getEnabled());

        User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(toDto(updatedUser));
    }

    private UserDTO toDto(User user) {
        UserDTO dto = new UserDTO();
        dto.setUserName(user.getUserName());
        dto.setUserFirstName(user.getUserFirstName());
        dto.setUserLastName(user.getUserLastName());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.getEnabled());
        dto.setRoles(user.getRoles());
        dto.setDateCreated(user.getDateCreated());
        return dto;
    }
}

