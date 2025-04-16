package com.focussu.backend.auth.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;
    // 토큰 저장 시 key 앞에 접두어를 붙여 관리 (예: TOKEN_{jwt})
    private static final String TOKEN_PREFIX = "TOKEN_";

    public TokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 로그인 시 생성된 토큰을 저장 (필요에 따라 만료 시간 설정도 가능)
    public void saveToken(String token, String username) {
        redisTemplate.opsForValue().set(TOKEN_PREFIX + token, username);
    }

    // 로그아웃 시 토큰 삭제
    public void removeToken(String token) {
        redisTemplate.delete(TOKEN_PREFIX + token);
    }

    // Redis에 토큰이 존재하는지 확인 (존재하면 유효한 로그인 상태로 간주)
    public boolean isTokenRevoked(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(TOKEN_PREFIX + token));
    }
}
