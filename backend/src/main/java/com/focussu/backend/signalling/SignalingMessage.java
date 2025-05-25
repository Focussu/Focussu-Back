package com.focussu.backend.signalling;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignalingMessage {
    private String type;        // "join"|"leave"|"offer"|"answer"|"candidate"
    private String roomId;      // ex: "room-1234"
    private String to;          // 1:1용 수신자 userId (필요 시)
    private JsonNode payload;

    public SignalingMessage(String type, String roomId, JsonNode payload) {
        this(type, roomId, null, payload);
    }
    // getters/setters...
}
