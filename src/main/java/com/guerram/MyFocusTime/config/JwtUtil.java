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
        // Clave secreta hardcodeada (para desarrollo)
        String secret = "Q8v#pT2wL9z!rF4xD7yM3kH6nU1sB0jA";
        SECRET_KEY = Keys.hmacShaKeyFor(secret.getBytes());
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
