package com.focussu.backend.document;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis-document")
public class AnalysisDocumentController {

    private final AnalysisDocumentService service;

    @Operation(summary = "보고서 생성", description = "티켓 번호와 내용으로 보고서를 생성합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @PostMapping
    public AnalysisDocumentResponse create(@RequestBody AnalysisDocumentRequest request) {
        return service.create(request);
    }

    @Operation(summary = "보고서 조회", description = "여러 티켓 번호로 보고서를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping
    public List<AnalysisDocumentResponse> getByTicketNumbers(@RequestParam List<Long> ticketNumbers) {
        return service.getByTicketNumbers(ticketNumbers);
    }

}
