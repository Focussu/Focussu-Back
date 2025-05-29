package com.focussu.backend.studyparticipation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "집중도 통계 응답")
public record ConcentrationStatsResponse(
        @Schema(description = "회원 ID") Long memberId,
        @Schema(description = "통계 시작일") LocalDate startDate,
        @Schema(description = "통계 종료일") LocalDate endDate,
        @Schema(description = "평균 집중도") Double averageConcentration
) {
    public static ConcentrationStatsResponse from(Long memberId, LocalDate start, LocalDate end, Double average) {
        return new ConcentrationStatsResponse(memberId, start, end, average);
    }
}
