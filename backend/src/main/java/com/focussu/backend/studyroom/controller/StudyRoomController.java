package com.focussu.backend.studyroom.controller;

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

    private final StudyRoomCommandService studyRoomCommandService;
    private final StudyRoomQueryService studyRoomQueryService;

    // StudyRoom 생성 API (POST /studyrooms?name=Study Room Name)
    @PostMapping
    public ResponseEntity<StudyRoom> createStudyRoom(@RequestParam String name) {
        StudyRoom studyRoom = studyRoomCommandService.createStudyRoom(name);
        return ResponseEntity.ok(studyRoom);
    }

    // StudyRoom 조회 API (GET /studyrooms/{id})
    @GetMapping("/{id}")
    public ResponseEntity<StudyRoom> getStudyRoom(@PathVariable Long id) {
        StudyRoom studyRoom = studyRoomQueryService.getStudyRoomById(id);
        return ResponseEntity.ok(studyRoom);
    }
}
