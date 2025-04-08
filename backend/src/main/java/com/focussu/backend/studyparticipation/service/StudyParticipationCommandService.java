package com.focussu.backend.studyparticipation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudyParticipationCommandService {

    private final RedisTemplate<String, String> redisTemplate;

    public void addParticipant(Long roomId, String userId) {
        String key = buildKey(roomId);
        redisTemplate.opsForSet().add(key, userId);
        log.info("✅ Redis addParticipant: key={}, userId={}", key, userId);
    }

    public void removeParticipant(Long roomId, String userId) {
        String key = buildKey(roomId);
        redisTemplate.opsForSet().remove(key, userId);
        log.info("❌ Redis removeParticipant: key={}, userId={}", key, userId);
    }

    private String buildKey(Long roomId) {
        return "studyroom:participants:" + roomId;
    }
}
