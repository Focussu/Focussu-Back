package com.focussu.backend.studyparticipation.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(description = "공부 시간/집중도 응답")
public record StudyParticipationResponse(

        @Schema(description = "회원 ID")
        Long memberId,

        @Schema(description = "측정 시작일")
        LocalDate startDate,

        @Schema(description = "측정 종료일 (또는 단일일 경우 동일)")
        LocalDate endDate,

        @Schema(description = "총 시간(초)")
        Long seconds

) {
    public static StudyParticipationResponse of(Long memberId, LocalDateTime start, LocalDateTime end, Long seconds) {
        return new StudyParticipationResponse(
                memberId,
                start.toLocalDate(),
                end.toLocalDate(),
                seconds
        );
    }

    public static StudyParticipationResponse ofTotal(Long memberId, Long seconds) {
        return new StudyParticipationResponse(
                memberId,
                null,
                null,
                seconds
        );
    }
}
