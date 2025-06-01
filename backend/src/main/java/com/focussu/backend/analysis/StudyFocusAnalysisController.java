package com.focussu.backend.analysis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Study Focus Analysis", description = "집중도 분석 수신 및 시간대별 변화 조회")
@RestController
@RequiredArgsConstructor
public class StudyFocusAnalysisController {

    private final StudyFocusAnalysisCommandService studyFocusAnalysisCommandService;
    private final StudyFocusAnalysisQueryService studyFocusAnalysisQueryService;

    @Operation(summary = "[AI 분석용] 분석 데이터 수신", description = "티켓 당 분석 결과를 수신합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @PostMapping("/ai-analysis")
    public ResponseEntity<Void> receiveAnalysisResult(@RequestBody AiAnalysisRequest dto) {
        studyFocusAnalysisCommandService.saveAnalysis(dto);
        studyFocusAnalysisCommandService.updateFocusAnalysis(dto.ticketNumber());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "티켓 아이디에 해당되는 로그 조회", description = "티켓 아이디에 해당되는 로그를 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/ai-analysis/{ticket_id}")
    public ResponseEntity<List<StudyFocusAnalysisResponse>> getFocusAnalysis(@PathVariable Long ticket_id) {
        return ResponseEntity.of(Optional.ofNullable(studyFocusAnalysisQueryService.getFocusAnalysis(ticket_id)));
    }

    @Operation(summary = "내 로그 조회", description = "현재 접속 멤버의 로그를 모두 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/ai-analysis/my")
    public ResponseEntity<List<StudyFocusAnalysisResponse>> getMyFocusAnalysis() {
        List<StudyFocusAnalysisResponse> result = studyFocusAnalysisQueryService.getMyAnalysisLogs();
        return ResponseEntity.ok(result);
    }
}
