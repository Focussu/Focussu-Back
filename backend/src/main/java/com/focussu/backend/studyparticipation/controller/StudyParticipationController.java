package com.focussu.backend.studyparticipation.controller;

import com.focussu.backend.studyparticipation.service.StudyParticipationCommandService;
import com.focussu.backend.studyparticipation.service.StudyParticipationQueryService;
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
public class StudyParticipationController {

    private final StudyParticipationCommandService commandService;
    private final StudyParticipationQueryService queryService;

    @GetMapping("/{roomId}/participants")
    public ResponseEntity<Set<String>> getParticipants(@PathVariable Long roomId) {
        Set<String> participants = queryService.getParticipants(roomId);
        return ResponseEntity.ok(participants);
    }
}
