package project.piuda.global.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secretKey;
    private Key key;
    private static final long ACCESS_TOKEN_VALIDITY_MS = 1000L * 60 * 30;       // 30분
    private static final long REFRESH_TOKEN_VALIDITY_DAYS = 14;                  // 14일

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(Long userId, String email, String role) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_VALIDITY_MS))
                .signWith(key)
                .compact();
    }

    public String createRefreshToken() {
        return java.util.UUID.randomUUID().toString();
    }

    public java.time.LocalDateTime getRefreshTokenExpiry() {
        return java.time.LocalDateTime.now().plusDays(REFRESH_TOKEN_VALIDITY_DAYS);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}