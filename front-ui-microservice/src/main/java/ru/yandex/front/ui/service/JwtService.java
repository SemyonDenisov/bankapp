package ru.yandex.front.ui.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
public class JwtService {


    private final String SECRET = "supersecretkeysupersecretkeysupersecretkey"; // минимум 256 бит
    private final long EXPIRATION = 1000 * 60 * 60; // 1 час

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());


    public String generateToken(String email) {
        return Jwts.builder()
                .header()
                .add("typ", "JWT")
                .and()
                .subject(email)
                .expiration(
                        Date.from(LocalDateTime.now()
                                .plusMinutes(EXPIRATION)
                                .atZone(ZoneId.systemDefault())
                                .toInstant()
                        )
                )
                .signWith(key)
                .compact();
    }

    public String extractEmail(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean validateToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration()
                    .after(new Date());
        } catch (Exception e) {
            return false;
        }
    }


}
