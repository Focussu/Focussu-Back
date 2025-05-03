package com.focussu.backend.auth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;

@Service
public class TokenService {

    private final RedisTemplate<String, String> redisTemplate;
    private final long expirationTimeSeconds; // 초 단위 만료 시간
    private static final String TOKEN_PREFIX = "TOKEN_";

    public TokenService(RedisTemplate<String, String> redisTemplate,
                        @Value("${security.jwt.expiration-time}") long expirationTimeSeconds) {
        this.redisTemplate = redisTemplate;
        this.expirationTimeSeconds = expirationTimeSeconds;
    }

    /**
     * 사용자 이름(username)을 키로, JWT 토큰을 값으로 Redis에 저장합니다.
     *
     * @param token    저장할 JWT 토큰
     * @param username 토큰에 해당하는 사용자 이름
     */
    public void saveToken(String token, String username) {
        String key = TOKEN_PREFIX + username;
        Duration duration = Duration.ofSeconds(expirationTimeSeconds);
        redisTemplate.opsForValue().set(key, token, duration);
    }

    /**
     * 사용자 이름(username)을 기반으로 Redis에서 토큰을 삭제합니다.
     *
     * @param username 삭제할 토큰의 사용자 이름
     */
    public void removeTokenByUsername(String username) {
        String key = TOKEN_PREFIX + username;
        redisTemplate.delete(key);
    }

    /**
     * 주어진 JWT 토큰 값으로 Redis에서 사용자 이름(username)을 찾아 반환합니다.
     * 주의: 운영 환경에서는 KEYS(*) 대신 SCAN 명령 사용을 권장합니다. (성능 이슈)
     *
     * @param token 찾고자 하는 JWT 토큰
     * @return Optional<String> 형태로 사용자 이름을 반환 (존재하지 않으면 Optional.empty())
     */
    public Optional<String> getUsernameByToken(String token) {
        ValueOperations<String, String> valueOps = redisTemplate.opsForValue();
        // "TOKEN_*" 패턴의 키 조회 (성능 주의)
        Set<String> keys = redisTemplate.keys(TOKEN_PREFIX + "*");

        if (keys != null) {
            for (String key : keys) {
                String storedToken = valueOps.get(key);
                if (token.equals(storedToken)) {
                    // 키에서 접두사 제거 후 username 반환
                    return Optional.of(key.substring(TOKEN_PREFIX.length()));
                }
            }
        }
        return Optional.empty(); // 토큰을 찾지 못함
    }

    /**
     * 주어진 JWT 토큰을 Redis에서 찾아 삭제합니다.
     *
     * @param token 삭제할 JWT 토큰
     */
    public void removeToken(String token) {
        getUsernameByToken(token).ifPresent(this::removeTokenByUsername);
    }

    /**
     * 주어진 JWT 토큰이 Redis에 유효하게 저장되어 있는지 확인합니다.
     *
     * @param token 확인할 JWT 토큰
     * @return 토큰이 존재하면 true, 아니면 false
     */
    public boolean isTokenNotRevoked(String token) {
        return getUsernameByToken(token).isPresent();
    }
}
