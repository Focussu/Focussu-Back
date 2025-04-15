package com.focussu.backend.studyparticipation.controller;

import com.focussu.backend.studyparticipation.service.StudyParticipationCommandService;
import com.focussu.backend.studyparticipation.service.StudyParticipationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/studyrooms/{roomId}/participants")
@RequiredArgsConstructor
@Tag(name = "study-participation-controller", description = "스터디 참여/퇴장 관련 API")
public class StudyParticipationController {

    private final StudyParticipationCommandService commandService;
    private final StudyParticipationQueryService queryService;

    @Operation(summary = "스터디룸 참가자 목록 조회", description = "해당 스터디룸의 현재 참여 중인 유저 목록을 반환합니다.")
    @GetMapping
    public ResponseEntity<Set<String>> getParticipants(@PathVariable Long roomId) {
        Set<String> participants = queryService.getParticipants(roomId);
        return ResponseEntity.ok(participants);
    }

    @Operation(summary = "스터디룸 참여 요청", description = "해당 스터디룸에 유저를 참여시킵니다.")
    @PostMapping("/{userId}")
    public ResponseEntity<Void> joinStudyRoom(@PathVariable Long roomId, @PathVariable String userId) {
        commandService.addParticipant(roomId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "스터디룸 퇴장 요청", description = "해당 스터디룸에서 유저를 제거합니다.")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> leaveStudyRoom(@PathVariable Long roomId, @PathVariable String userId) {
        commandService.removeParticipant(roomId, userId);
        return ResponseEntity.noContent().build();
    }
}
