package com.abc.SpringBootSecqurityEx.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {
    @GetMapping("/my-roles")
    @PreAuthorize("isAuthenticated()")
    public Map<String, Object> getMyRoles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Map<String, Object> result = new HashMap<>();
        result.put("username", authentication.getName());
        result.put("authorities", authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList());
        result.put("authenticated", authentication.isAuthenticated());
        return result;
    }
}
