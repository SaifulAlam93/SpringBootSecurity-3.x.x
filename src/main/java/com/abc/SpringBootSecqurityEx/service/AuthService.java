package com.abc.SpringBootSecqurityEx.service;

import com.abc.SpringBootSecqurityEx.dtos.JwtResponse;
import com.abc.SpringBootSecqurityEx.dtos.LoginRequest;
import com.abc.SpringBootSecqurityEx.dtos.RegisterRequest;
import com.abc.SpringBootSecqurityEx.dtos.UserDTO;
import com.abc.SpringBootSecqurityEx.entity.User;
import com.abc.SpringBootSecqurityEx.enums.ERole;
import com.abc.SpringBootSecqurityEx.repository.UserRepository;
import com.abc.SpringBootSecqurityEx.secqurity.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumSet;

@Service
@Transactional
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public JwtResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtUtil.generateToken(loginRequest.getUsername());

        User user = userRepository.findByUserName(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setUserFirstName(user.getUserFirstName());
        userDTO.setUserLastName(user.getUserLastName());
        userDTO.setEmail(user.getEmail());
        userDTO.setEnabled(user.getEnabled());
        userDTO.setRoles(user.getRoles());
        userDTO.setDateCreated(user.getDateCreated());
        return new JwtResponse(jwt, userDTO);
    }

    public String register(RegisterRequest registerRequest) {
        validateRegistration(registerRequest);

        User user = createUserFromRequest(registerRequest);
        userRepository.save(user);
        return "User registered successfully";
    }

    private void validateRegistration(RegisterRequest registerRequest) {
        if (userRepository.existsByUserName(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    private User createUserFromRequest(RegisterRequest registerRequest) {
        User user = new User();
        user.setUserName(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setUserFirstName(registerRequest.getFirstName());
        user.setUserLastName(registerRequest.getLastName());

        // Set default values
        user.setEnabled(true);
        user.setAccountNonExpired(true);
        user.setCredentialsNonExpired(true);
        user.setAccountNonLocked(true);
        user.setRoles(EnumSet.of(ERole.ROLE_USER));

        return user;
    }

}
