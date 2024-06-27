package com.neweb.web.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private int jwtExpirationMs = 86400000; // 设置过期时间，这里设置为1天

    @PostConstruct
    public void init() {
        try {
            logger.info("Initializing JwtUtils...");
            this.key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
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
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean validateJwtToken(String authToken) {
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
