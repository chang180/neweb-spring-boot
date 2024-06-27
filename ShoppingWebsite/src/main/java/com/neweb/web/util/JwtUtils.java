package com.neweb.web.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.neweb.web.service.UserDetailsImpl;

import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    private Key key;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expirationMs}")
    private int jwtExpirationMs;

    @PostConstruct
    public void init() {
        try {
            logger.info("Initializing JwtUtils...");
            if (jwtSecret.length() < 64) {
                throw new IllegalArgumentException("The JWT secret key must be at least 64 bytes.");
            }
            byte[] keyBytes = jwtSecret.getBytes();
            this.key = Keys.hmacShaKeyFor(keyBytes);
            logger.info("JwtUtils initialized successfully with secret.");
        } catch (Exception e) {
            logger.error("Error initializing JwtUtils", e);
            throw e;
        }
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (JwtException e) {
            logger.error("Failed to parse JWT token: {}", e.getMessage());
            return null;
        }
    }

    public boolean validateJwtToken(String authToken) {
        if (authToken == null || authToken.isEmpty() || !authToken.contains(".")) {
            logger.error("JWT token is empty, null, or does not contain 2 period characters");
            return false;
        }
        
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }

        return false;
    }

    public int getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
