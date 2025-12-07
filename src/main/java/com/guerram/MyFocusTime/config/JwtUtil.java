package com.guerram.MyFocusTime.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final Key SECRET_KEY;

    public JwtUtil() {
        // Leer la clave de entorno
        String secret = System.getenv("JWT_SECRET");
        if (secret == null || secret.isEmpty()) {
            throw new RuntimeException("JWT_SECRET no está configurada en las variables de entorno");
        }
        SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes()); // Crear Key a partir de la variable
    }

    public String generateToken(Long userId, String mail){
        return Jwts.builder()
                .setSubject(mail)
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis()+1000 * 60 *60 *10)) // 10hs
                .signWith(SECRET_KEY)
                .compact();
    }

    public Long extractUserId(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    public String extractMail(String token){
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
