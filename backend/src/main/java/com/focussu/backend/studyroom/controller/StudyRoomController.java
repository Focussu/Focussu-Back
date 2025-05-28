package com.focussu.backend.studyroom.controller;

import com.focussu.backend.studyroom.dto.StudyRoomCreateRequest;
import com.focussu.backend.studyroom.dto.StudyRoomCreateResponse;
import com.focussu.backend.studyroom.dto.StudyRoomJoinResponse;
import com.focussu.backend.studyroom.service.StudyRoomCommandService;
import com.focussu.backend.studyroom.service.StudyRoomQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "스터디룸", description = "StudyRoom 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/studyrooms")
public class StudyRoomController {

    private final StudyRoomCommandService commandService;
    private final StudyRoomQueryService queryService;

    @Operation(summary = "스터디룸 생성", description = "새로운 스터디룸을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping
    public ResponseEntity<StudyRoomCreateResponse> createStudyRoom(@RequestBody StudyRoomCreateRequest request) {
        return ResponseEntity.ok(commandService.createStudyRoom(request));
    }

    @Operation(summary = "스터디룸 전체 조회", description = "모든 스터디룸 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<List<StudyRoomCreateResponse>> getStudyRooms() {
        return ResponseEntity.ok(queryService.getStudyRooms());
    }

    @Operation(summary = "스터디룸 단일 조회", description = "ID에 해당하는 스터디룸을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "스터디룸을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudyRoomCreateResponse> getStudyRoom(@PathVariable Long id) {
        return ResponseEntity.ok(queryService.getStudyRoom(id));
    }

    @Operation(summary = "스터디룸 참가", description = "ID에 해당하는 스터디룸에 참가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "참가 성공"),
            @ApiResponse(responseCode = "404", description = "스터디룸을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/join/{id}")
    public ResponseEntity<StudyRoomJoinResponse> joinStudyRoom(@PathVariable Long id) {
        return ResponseEntity.ok(commandService.joinStudyRoom(id));
    }

    @Operation(summary = "내가 참가한 스터디룸 조회", description = "내가 참가한 스터디룸을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "스터디룸을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/my")
    public ResponseEntity<List<StudyRoomCreateResponse>> getMyStudyRooms() {
        return ResponseEntity.ok(queryService.getMyStudyRooms());
    }
}
