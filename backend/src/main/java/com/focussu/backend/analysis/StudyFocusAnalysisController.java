package com.focussu.backend.analysis;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class StudyFocusAnalysisController {

    private final StudyFocusAnalysisCommandService studyFocusAnalysisCommandService;

    @Operation(summary = "[AI 분석용] 분석 데이터 수신", description = "티켓 당 분석 결과를 수신합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @PostMapping("/ai-analysis")
    public ResponseEntity<Void> receiveAnalysisResult(@RequestBody AiAnalysisRequest dto){
        studyFocusAnalysisCommandService.saveAnalysis(dto);
        studyFocusAnalysisCommandService.updateFocusAnalysis(dto.ticketNumber());
        return ResponseEntity.ok().build();
    }
}
