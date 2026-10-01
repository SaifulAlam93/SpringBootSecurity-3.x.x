package com.abc.SpringBootSecqurityEx.secqurity;

import com.abc.SpringBootSecqurityEx.entity.User;
import com.abc.SpringBootSecqurityEx.enums.ERole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String SECRET_KEY;

    @Value("${app.jwt.expiration}")
    private long EXPIRATION_TIME;

    @PostConstruct
    void validateConfiguration() {

        if (EXPIRATION_TIME <= 0) {
            throw new IllegalStateException(
                    "JWT_EXPIRATION must be positive"
            );
        }

        // Validate secret during application startup
        getSigningKey();
    }

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(SECRET_KEY)
        );
    }

    // =========================================================
    // Generate Token
    // =========================================================

    public String generateToken(String username) {

        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }

    // =========================================================
    // Generate Token From User
    // =========================================================

    public String generateTokenFromUsername(User user) {

        return Jwts.builder()
                .subject(user.getUserName())
                .claim(
                        "roles",
                        user.getRoles()
                                .stream()
                                .map(ERole::name)
                                .collect(Collectors.toList())
                )
                .claim("email", user.getEmail())
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + EXPIRATION_TIME
                        )
                )
                .signWith(getSigningKey())
                .compact();
    }

    // =========================================================
    // Validate Token
    // =========================================================

    public boolean validateToken(String token) {

        try {

            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);

            return true;

        } catch (JwtException | IllegalArgumentException e) {

            return false;
        }
    }

    // =========================================================
    // Extract Username
    // =========================================================

    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // =========================================================
    // Extract Claims
    // =========================================================

    public Claims extractAllClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
