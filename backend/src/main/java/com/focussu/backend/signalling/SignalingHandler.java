package com.focussu.backend.signalling;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class SignalingHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Set<String>> rooms = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final Map<String, ExecutorService> executors = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String userId = (String) session.getAttributes().get("userId");
        sessions.put(userId, session);
        // single-threaded executor per session to serialize sends
        executors.put(userId, Executors.newSingleThreadExecutor());
        log.info("[Signaling] CONNECTED: {}", userId);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage msg) throws IOException {
        SignalingMessage in = mapper.readValue(msg.getPayload(), SignalingMessage.class);
        String from = (String) session.getAttributes().get("userId");
        String roomId = in.getRoomId();
        log.info("[Signaling] [{}] {} ▶ {}", roomId, from, in.getType());

        switch (in.getType()) {
            case JOIN:
                handleJoin(from, roomId);
                break;

            case LEAVE:
                handleLeave(from, roomId);
                break;

            case OFFER:
            case ANSWER:
            case CANDIDATE:
            case PING:
                if (in.getTo() != null) {
                    sendEventToUser(in.getTo(), in.getType(), roomId, in.getPayload(), from);
                } else {
                    broadcastToRoom(roomId, in.getType(), in.getPayload(), from);
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
        // shutdown executor
        ExecutorService exec = executors.remove(userId);
        if (exec != null) exec.shutdownNow();

        rooms.values().forEach(m -> m.remove(userId));
        log.info("[Signaling] DISCONNECTED: {}", userId);
    }

    private void handleJoin(String userId, String roomId) throws IOException {
        Set<String> participants = rooms.computeIfAbsent(roomId, r -> ConcurrentHashMap.newKeySet());
        // (1) 신규 사용자에게 기존 참여자 목록 전송
        ObjectNode listPayload = mapper.createObjectNode();
        ArrayNode peersArray = listPayload.putArray("peers");
        participants.forEach(peersArray::add);
        sendEventToUser(userId, MessageType.JOINED, roomId, listPayload, "server");

        // (2) 기존 참여자에게 신규 사용자 알림
        ObjectNode newPeerPayload = createPayload("from", userId);
        for (String peer : participants) {
            sendEventToUser(peer, MessageType.NEW_PEER, roomId, newPeerPayload, userId);
        }
        participants.add(userId);
    }

    private void handleLeave(String userId, String roomId) throws IOException {
        Set<String> participants = rooms.getOrDefault(roomId, Collections.emptySet());
        participants.remove(userId);
        broadcastToRoom(roomId, MessageType.PEER_LEFT, createPayload("from", userId), userId);
    }

    private ObjectNode createPayload(String key, String value) {
        return mapper.createObjectNode().put(key, value);
    }

    private void sendEventToUser(String targetId,
                                 MessageType type,
                                 String roomId,
                                 Object rawPayload,
                                 String from) {
        WebSocketSession session = sessions.get(targetId);
        JsonNode payloadNode = mapper.valueToTree(rawPayload);
        ((ObjectNode) payloadNode).put("from", from);

        SignalingMessage msg = new SignalingMessage(type, roomId, targetId, payloadNode);
        if (session != null && session.isOpen()) {
            ExecutorService exec = executors.get(targetId);
            if (exec != null) {
                exec.submit(() -> {
                    try {
                        session.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
                        log.info("[Signaling] SEND to {} in room {}: {}", targetId, roomId, type);
                    } catch (IOException e) {
                        log.error("[Signaling] Failed to send message to {}", targetId, e);
                    }
                });
            }
        } else {
            log.warn("[Signaling] Cannot send to {} (closed or absent)", targetId);
            WebSocketSession sender = sessions.get(from);
            if (sender != null && sender.isOpen()) {
                ObjectNode errorNode = createPayload("message", "target not available");
                SignalingMessage errorMsg = new SignalingMessage(MessageType.ERROR, roomId, targetId, errorNode);
                ExecutorService exec = executors.get(from);
                if (exec != null) {
                    exec.submit(() -> {
                        try {
                            sender.sendMessage(new TextMessage(mapper.writeValueAsString(errorMsg)));
                        } catch (IOException ex) {
                            log.error("[Signaling] Failed to send error to {}", from, ex);
                        }
                    });
                }
            }
        }
    }

    private void broadcastToRoom(String roomId,
                                 MessageType type,
                                 Object rawPayload,
                                 String from) {
        JsonNode basePayload = mapper.valueToTree(rawPayload);
        for (String peer : rooms.getOrDefault(roomId, Collections.emptySet())) {
            if (!peer.equals(from)) {
                sendEventToUser(peer, type, roomId, basePayload, from);
            }
        }
        log.info("[Signaling] BROADCAST to room {}: {}", roomId, type);
    }
}
