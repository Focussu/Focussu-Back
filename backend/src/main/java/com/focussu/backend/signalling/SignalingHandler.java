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

import static com.focussu.backend.signalling.MessageType.ERROR;

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
            case JOIN:
                rooms.computeIfAbsent(roomId, r -> ConcurrentHashMap.newKeySet()).add(from);

                // (1) 기존 유저 목록 전송
                Set<String> currentPeers = rooms.get(roomId);
                ArrayNode peerArray = mapper.createArrayNode();
                for (String peer : currentPeers) {
                    if (!peer.equals(from)) {
                        peerArray.add(peer);
                    }
                }
                ObjectNode joinedPayload = mapper.createObjectNode();
                joinedPayload.set("peers", peerArray);
                session.sendMessage(new TextMessage(mapper.writeValueAsString(
                        new SignalingMessage(MessageType.JOINED, roomId, null, joinedPayload)
                )));

                // (2) 기존 유저들에게 new-peer 알리기
                ObjectNode joinPayload = mapper.createObjectNode().put("from", from);
                SignalingMessage newPeerMsg = new SignalingMessage(MessageType.NEW_PEER, roomId, null, joinPayload);
                broadcast(roomId, newPeerMsg, from);
                break;

            case LEAVE:
                Set<String> members = rooms.getOrDefault(roomId, Collections.emptySet());
                members.remove(from);
                ObjectNode leavePayload = mapper.createObjectNode().put("from", from);
                SignalingMessage leftMsg = new SignalingMessage(MessageType.PEER_LEFT, roomId, null, leavePayload);
                broadcast(roomId, leftMsg, from);
                break;

            case OFFER:
            case ANSWER:
            case CANDIDATE:
            case PING:
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

        JsonNode payloadNode = mapper.convertValue(msg.getPayload(), JsonNode.class);
        ((ObjectNode) payloadNode).put("from", sender);
        msg.setPayload(payloadNode);

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

        JsonNode payloadNode = mapper.convertValue(msg.getPayload(), JsonNode.class);
        ((ObjectNode) payloadNode).put("from", from);
        msg.setPayload(payloadNode);

        WebSocketSession ws = sessions.get(to);
        if (ws != null && ws.isOpen() && rooms.getOrDefault(roomId, Collections.emptySet()).contains(to)) {
            ws.sendMessage(new TextMessage(mapper.writeValueAsString(msg)));
            log.info("[Signaling] SEND to {} in room {}: {}", to, roomId, msg.getType());
        } else {
            log.warn("[Signaling] Cannot send to {} (not in room or closed)", to);
            WebSocketSession senderSession = sessions.get(from);
            if (senderSession != null && senderSession.isOpen()) {
                ObjectNode errorPayload = mapper.createObjectNode().put("message", "target not available");
                senderSession.sendMessage(new TextMessage(mapper.writeValueAsString(
                        new SignalingMessage(ERROR, roomId, to, errorPayload)
                )));
            }
        }
    }


}