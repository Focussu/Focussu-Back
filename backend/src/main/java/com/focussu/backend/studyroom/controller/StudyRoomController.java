package com.focussu.backend.studyroom.controller;

import com.focussu.backend.studyroom.dto.StudyRoomCreateRequest;
import com.focussu.backend.studyroom.dto.StudyRoomCreateResponse;
import com.focussu.backend.studyroom.model.StudyRoom;
import com.focussu.backend.studyroom.service.StudyRoomCommandService;
import com.focussu.backend.studyroom.service.StudyRoomQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/studyrooms")
public class StudyRoomController {

    private final StudyRoomCommandService commandService;
    private final StudyRoomQueryService queryService;

    // StudyRoom 생성 API (POST /studyrooms?name=Study Room Name)
    @PostMapping
    public ResponseEntity<StudyRoomCreateResponse> createStudyRoom(@RequestBody StudyRoomCreateRequest request) {
        return ResponseEntity.ok(commandService.createStudyRoom(request));
    }

    // StudyRoom 조회 API (GET /studyrooms/{id})
    @GetMapping("/{id}")
    public ResponseEntity<StudyRoomCreateResponse> getStudyRoom(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getStudyRoom(id));
    }
}
