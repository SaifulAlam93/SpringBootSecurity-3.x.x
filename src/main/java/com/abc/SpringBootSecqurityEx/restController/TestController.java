package com.abc.SpringBootSecqurityEx.restController;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('USER', 'MODERATOR', 'ADMIN')")
    public Map<String, String> allAccess() {
        return Map.of("message", "User content.");
    }

    @GetMapping("/mod")
    @PreAuthorize("hasRole('MODERATOR')")
    public Map<String, String> moderatorAccess() {
        return Map.of("message", "Moderator board.");
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> adminAccess() {
        return Map.of("message", "Admin board.");
    }
}
