package com.focussu.backend.studyroomparticipant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.focussu.backend.studyroomparticipant.service.StudyRoomParticipantQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

@ExtendWith(MockitoExtension.class)
public class StudyRoomParticipantKafkaIntegrationTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private SetOperations<String, String> setOperations;

    private StudyRoomParticipantQueryService queryService;

    @BeforeEach
    public void setup() {
        // redisTemplate.opsForSet() 호출 시 setOperations 목 객체 반환하도록 설정
        when(redisTemplate.opsForSet()).thenReturn(setOperations);
        queryService = new StudyRoomParticipantQueryService(redisTemplate);
    }

    @Test
    public void testGetParticipants() {
        Long roomId = 1L;
        String key = "studyroom:participants:" + roomId;
        Set<String> expectedParticipants = new HashSet<>(Arrays.asList("user123"));

        // setOperations.members(key) 호출 시 expectedParticipants 반환하도록 설정
        when(setOperations.members(key)).thenReturn(expectedParticipants);

        Set<String> actualParticipants = queryService.getParticipants(roomId);
        assertEquals(expectedParticipants, actualParticipants);
    }
}
