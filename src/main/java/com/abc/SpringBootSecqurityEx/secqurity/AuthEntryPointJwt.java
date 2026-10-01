package com.abc.SpringBootSecqurityEx.secqurity;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        logger.error("Unauthorized error: {}", authException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        final Map<String, Object> body = new HashMap<>();
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("timestamp", new Date());
        body.put("error", "Unauthorized");
        body.put("message", getCustomMessage(authException, request));
        body.put("path", request.getServletPath());
        body.put("method", request.getMethod());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }


    private String getCustomMessage(AuthenticationException authException, HttpServletRequest request) {
        String message = authException.getMessage();

        // Check for specific JWT-related issues
        if (message != null && (message.contains("JWT") || message.contains("token"))) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null) {
                return "Missing Authorization header";
            } else if (!authHeader.startsWith("Bearer ")) {
                return "Invalid Authorization header format. Expected 'Bearer <token>'";
            } else {
                return "Invalid or expired JWT token";
            }
        }

        return "Authentication failed: " + message;
    }
}
