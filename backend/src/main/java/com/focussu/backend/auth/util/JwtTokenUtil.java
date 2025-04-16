package com.focussu.backend.auth.util;

import com.focussu.backend.auth.exception.AuthException;
import com.focussu.backend.common.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    private final SecretKey secretKey;

    public JwtTokenUtil(@Value("${security.jwt.secret-key}") String secret) {
        // secretKey는 HMAC을 위해 설정됨
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 토큰 생성 (10시간 유효)
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long validityInMs = 1000 * 60 * 60 * 10;  // 10시간
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validityInMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // 토큰에서 username(Subject) 추출
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // 토큰 파싱 시 발생하는 세부 예외에 따라 커스텀 예외(AuthException) throw
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new AuthException(ErrorCode.AUTH_TOKEN_EXPIRED);
        } catch (SignatureException e) {
            throw new AuthException(ErrorCode.AUTH_TOKEN_INVALID_SIGNATURE);
        } catch (MalformedJwtException e) {
            throw new AuthException(ErrorCode.AUTH_TOKEN_MALFORMED);
        } catch (Exception e) {
            throw new AuthException(ErrorCode.AUTH_TOKEN_MALFORMED);
        }
    }

    // 토큰 만료 여부 검사
    private Boolean isTokenExpired(String token) {
        final Date expiration = getClaimFromToken(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    // 토큰 유효성 검증 (username 일치 및 만료 여부 체크)
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
