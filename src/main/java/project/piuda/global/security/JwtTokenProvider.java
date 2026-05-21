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
    private final long tokenValidityInMilliseconds = 1000L * 60 * 30; // 30분

    @PostConstruct
    protected void init() {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(Long userId, String email, String role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + tokenValidityInMilliseconds);

        // 최신 JJWT 빌더 패턴 적용
        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .claim("role", role)
                .issuedAt(now)
                .expiration(validity)
                .signWith(key) // 알고리즘 인자 제거 (Key 스펙에 따라 자동 지정)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            // parserBuilder() -> parser()로 변경
            Jwts.parser().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getEmail(String token) {
        // parserBuilder() -> parser()로 변경
        return Jwts.parser().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}