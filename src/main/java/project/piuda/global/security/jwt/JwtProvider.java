package project.piuda.global.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;

@Component
public class JwtProvider {

    private final String SECRET = "your-secret-key-your-secret-key";
    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String createToken(Long userId) {
        return Jwts.builder()
                .subject(String.valueOf(userId))
                .signWith(key)
                .compact();
    }

    public Long getUserId(String token) {

        Claims claims = Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }
}