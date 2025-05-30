package com.focussu.backend.studyparticipation.controller;

import com.focussu.backend.auth.util.AuthUtil;
import com.focussu.backend.studyparticipation.dto.ConcentrationStatsResponse;
import com.focussu.backend.studyparticipation.dto.StudyParticipationResponse;
import com.focussu.backend.studyparticipation.service.StudyParticipationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Tag(name = "Study Participation", description = "공부 시간 및 집중도 통계 API")
@RestController
@RequestMapping("/study")
@RequiredArgsConstructor
public class StudyParticipationController {

    private final StudyParticipationQueryService queryService;
    private final AuthUtil authUtil;

    @Operation(summary = "오늘 공부 시간", description = "오늘 하루 동안의 공부 시간을 초 단위로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/time/today")
    public StudyParticipationResponse getTodayStudySeconds() {
        Long memberId = authUtil.getCurrentMemberId();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return queryService.buildStudyTimeResponse(memberId, start, end);
    }

    @Operation(summary = "이번 주 공부 시간", description = "이번 주(월~오늘) 동안의 공부 시간을 초 단위로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/time/week")
    public StudyParticipationResponse getThisWeekStudySeconds() {
        Long memberId = authUtil.getCurrentMemberId();
        LocalDateTime start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        return queryService.buildStudyTimeResponse(memberId, start, end);
    }

    @Operation(summary = "총 공부 시간", description = "지금까지의 누적 공부 시간을 초 단위로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/time/total")
    public StudyParticipationResponse getTotalStudySeconds() {
        Long memberId = authUtil.getCurrentMemberId();
        return queryService.buildStudyTimeTotalResponse(memberId);
    }

    @Operation(summary = "오늘 집중 시간", description = "오늘 하루 동안 집중한 시간을 초 단위로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/focused-time/today")
    public StudyParticipationResponse getTodayFocusedTime() {
        Long memberId = authUtil.getCurrentMemberId();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return queryService.buildFocusedTimeResponse(memberId, start, end);
    }

    @Operation(summary = "이번 주 집중 시간", description = "이번 주 동안 집중한 시간을 초 단위로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/focused-time/week")
    public StudyParticipationResponse getThisWeekFocusedTime() {
        Long memberId = authUtil.getCurrentMemberId();
        LocalDateTime start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        return queryService.buildFocusedTimeResponse(memberId, start, end);
    }

    @Operation(summary = "총 집중 시간", description = "누적 집중 시간을 초 단위로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/focused-time/total")
    public StudyParticipationResponse getTotalFocusedTime() {
        Long memberId = authUtil.getCurrentMemberId();
        return queryService.buildFocusedTimeTotalResponse(memberId);
    }

    @Operation(summary = "오늘 평균 집중도", description = "오늘 하루의 평균 집중도 점수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/concentration/today")
    public ConcentrationStatsResponse getTodayConcentration() {
        Long memberId = authUtil.getCurrentMemberId();
        LocalDateTime start = LocalDate.now().atStartOfDay();
        LocalDateTime end = start.plusDays(1);
        return queryService.buildConcentrationResponse(memberId, start, end);
    }

    @Operation(summary = "이번 주 평균 집중도", description = "이번 주(월~오늘)의 평균 집중도 점수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/concentration/week")
    public ConcentrationStatsResponse getThisWeekConcentration() {
        Long memberId = authUtil.getCurrentMemberId();
        LocalDateTime start = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime end = LocalDate.now().plusDays(1).atStartOfDay();
        return queryService.buildConcentrationResponse(memberId, start, end);
    }

    @Operation(summary = "총 평균 집중도", description = "지금까지의 평균 집중도 점수를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/concentration/total")
    public ConcentrationStatsResponse getTotalConcentration() {
        Long memberId = authUtil.getCurrentMemberId();
        return queryService.buildConcentrationTotalResponse(memberId);
    }

    @Operation(summary = "최근 접속까지 걸린 시간", description = "최근 스터디룸 퇴장 시각 이후 현재까지의 시간(초)을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "성공")
    @GetMapping("/inactive-seconds")
    public StudyParticipationResponse getInactiveSeconds() {
        Long memberId = authUtil.getCurrentMemberId();
        return queryService.buildInactiveSecondsResponse(memberId);
    }
}
