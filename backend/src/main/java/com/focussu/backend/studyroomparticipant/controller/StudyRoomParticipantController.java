package com.focussu.backend.studyroomparticipant.controller;

import com.focussu.backend.studyroomparticipant.service.StudyRoomParticipantCommandService;
import com.focussu.backend.studyroomparticipant.service.StudyRoomParticipantQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/study-rooms")
@RequiredArgsConstructor
public class StudyRoomParticipantController {

    private final StudyRoomParticipantCommandService commandService;
    private final StudyRoomParticipantQueryService queryService;

    @GetMapping("/{roomId}/participants")
    public ResponseEntity<Set<String>> getParticipants(@PathVariable Long roomId) {
        Set<String> participants = queryService.getParticipants(roomId);
        return ResponseEntity.ok(participants);
    }
}
