package com.focussu.backend.signalling;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SignalingHandler extends TextWebSocketHandler {
    // 사용자ID → WebSocketSession 맵
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    // 방ID → 사용자ID 세트
    private final Map<String, Set<String>> rooms = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");
        sessions.put(userId, session);
        log.info("[Signaling] CONNECTED: {}", userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage msg) throws IOException {
        SignalingMessage in = mapper.readValue(msg.getPayload(), SignalingMessage.class);
        String from = (String) session.getAttributes().get("userId");
        String roomId = in.getRoomId();
        log.info("[Signaling] [{}] {} ▶ {}", roomId, from, in.getType());

        switch (in.getType()) {
            case "join":
                // (1) 방 가입
                rooms.computeIfAbsent(roomId, r -> ConcurrentHashMap.newKeySet()).add(from);
                // (2) 기존 멤버에게 new-peer 브로드캐스트
                ObjectNode joinPayload = mapper.createObjectNode().put("from", from);
                SignalingMessage newPeerMsg = new SignalingMessage("new-peer", roomId, null, joinPayload);
                broadcast(roomId, newPeerMsg, from);
                break;

            case "leave":
                // (1) 방 탈퇴
                Set<String> members = rooms.getOrDefault(roomId, Collections.emptySet());
                members.remove(from);
                // (2) peer-left 방송
                ObjectNode leavePayload = mapper.createObjectNode().put("from", from);
                SignalingMessage leftMsg = new SignalingMessage("peer-left", roomId, null, leavePayload);
                broadcast(roomId, leftMsg, from);
                break;

            case "offer":
            case "answer":
            case "candidate":
            case "ping":
                // 1:1 대상 있으면 sendTo, 없으면 룸 브로드캐스트
                if (in.getTo() != null) {
                    sendTo(roomId, in.getTo(), from, in);
                } else {
                    broadcast(roomId, in, from);
                }
                break;

            default:
                log.warn("[Signaling] Unknown message type: {}", in.getType());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = (String) session.getAttributes().get("userId");
        sessions.remove(userId);
        rooms.values().forEach(m -> m.remove(userId));
        log.info("[Signaling] DISCONNECTED: {}", userId);
    }

    /**
     * 룸 내 전체 브로드캐스트 (sender 제외)
     */
    private void broadcast(String roomId, SignalingMessage msg, String sender) throws IOException {
        if (msg.getPayload() == null) {
            msg.setPayload(mapper.createObjectNode());
        }
        ((ObjectNode) msg.getPayload()).put("from", sender);

        for (String to : rooms.getOrDefault(roomId, Collections.emptySet())) {
            if (!to.equals(sender)) {
                WebSocketSession ws = sessions.get(to);
                if (ws != null && ws.isOpen()) {
                    ws.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
                }
            }
        }
        log.info("[Signaling] BROADCAST to room {}: {}", roomId, msg.getType());
    }

    /**
     * 룸 내 특정 유저에게 전송
     */
    private void sendTo(String roomId, String to, String from, SignalingMessage msg) throws IOException {
        if (msg.getPayload() == null) {
            msg.setPayload(mapper.createObjectNode());
        }
        ((ObjectNode) msg.getPayload()).put("from", from);

        WebSocketSession ws = sessions.get(to);
        if (ws != null && ws.isOpen() && rooms.getOrDefault(roomId, Collections.emptySet()).contains(to)) {
            ws.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
            log.info("[Signaling] SEND to {} in room {}: {}", to, roomId, msg.getType());
        } else {
            log.warn("[Signaling] Cannot send to {} (not in room or closed)", to);
        }
    }
}