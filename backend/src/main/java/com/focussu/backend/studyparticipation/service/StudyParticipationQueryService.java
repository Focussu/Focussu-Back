package com.focussu.backend.studyparticipation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class StudyParticipationQueryService {

    private final RedisTemplate<String, String> redisTemplate;

    public Set<String> getParticipants(Long roomId) {
        return redisTemplate.opsForSet().members(buildKey(roomId));
    }

    private String buildKey(Long roomId) {
        return "studyroom:participants:" + roomId;
    }
}
