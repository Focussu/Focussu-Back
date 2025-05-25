package com.focussu.backend.signalling;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignalingMessage {
    private MessageType type;
    private String roomId;
    private String to;
    private Object payload;

    @JsonCreator
    public SignalingMessage(
            @JsonProperty("type") MessageType type,
            @JsonProperty("roomId") String roomId,
            @JsonProperty("to") String to,
            @JsonProperty("payload") Object payload
    ) {
        this.type = type;
        this.roomId = roomId;
        this.to = to;
        this.payload = payload;
    }
}
