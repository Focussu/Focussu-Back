package com.focussu.backend.studyroomparticipant.kafka;

import com.focussu.backend.studyroomparticipant.service.StudyRoomParticipantCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaParticipantListener {

    private final StudyRoomParticipantCommandService commandService;

    @KafkaListener(topics = "mediasoup.user.connected", groupId = "participant-group")
    public void onUserConnected(String message) {
        try {
            JSONObject json = new JSONObject(message);
            Long roomId = Long.valueOf(json.get("roomId").toString());
            String userId = json.getString("userId");

            log.info("✅ Kafka consumed [connected]: roomId={}, userId={}", roomId, userId);
            commandService.addParticipant(roomId, userId);
        } catch (JSONException e) {
            log.error("❌ Failed to parse Kafka message: {}", message, e);
        }
    }


    @KafkaListener(topics = "mediasoup.user.disconnected", groupId = "participant-group")
    public void onUserDisconnected(String message) {
        try {
            JSONObject json = new JSONObject(message);
            Long roomId = json.getLong("roomId");
            String userId = json.getString("userId");

            commandService.removeParticipant(roomId, userId);
            log.info("✅ Kafka consumed [disconnected]: roomId={}, userId={}", roomId, userId);
        } catch (JSONException e) {
            log.error("❌ Failed to parse Kafka message [disconnected]: {}", message, e);
        }
    }
}
