package com.lasa.gloria.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:1800000}")
    private long expirationMs;

    @Value("${jwt.cookie.name:JWT}")
    private String cookieName;

    @Value("${jwt.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${jwt.cookie.same-site:Lax}")
    private String cookieSameSite;

    @Value("${jwt.cookie.path:/}")
    private String cookiePath;

    private static final String EXAMPLE_SECRET = "gloria-inventory-secret-key-change-in-production-1234567890";

    @PostConstruct
    void validateSecret() {
        if (secret == null || secret.isBlank() || EXAMPLE_SECRET.equals(secret)) {
            throw new IllegalStateException(
                    "JWT_SECRET no configurado: define la variable de entorno JWT_SECRET con un valor propio (>=32 caracteres). La app no arranca con el secreto de ejemplo ni vacío.");
        }
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    public String getCookieName() {
        return cookieName;
    }

    public boolean isCookieSecure() {
        return cookieSecure;
    }

    public String getCookieSameSite() {
        return cookieSameSite;
    }

    public String getCookiePath() {
        return cookiePath;
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String username, Integer userId) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        var builder = Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiry)
                .id(UUID.randomUUID().toString());
        if (userId != null) {
            builder.claim("userId", userId);
        }
        return builder.signWith(key()).compact();
    }

    /** @deprecated use {@link #generateToken(String, Integer)} to include userId claim */
    @Deprecated
    public String generateToken(String username) {
        return generateToken(username, null);
    }

    public String extractUsername(String token) {
        return parse(token).getSubject();
    }

    public Integer extractUserId(String token) {
        Claims claims = parse(token);
        Object v = claims.get("userId");
        if (v instanceof Integer i) return i;
        if (v instanceof Number n) return n.intValue();
        return null;
    }

    public boolean isTokenValid(String token, String username) {
        try {
            Claims claims = parse(token);
            return username.equals(claims.getSubject()) && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(key())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
