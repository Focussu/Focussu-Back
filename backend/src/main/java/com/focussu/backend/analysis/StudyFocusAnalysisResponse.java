package com.focussu.backend.analysis;

import java.time.LocalDateTime;

public record StudyFocusAnalysisResponse(
        Long ticketNumber,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Double score
) {
    public static StudyFocusAnalysisResponse fromEntity(StudyFocusAnalysis entity) {
        return new StudyFocusAnalysisResponse(
                entity.getTicketNumber(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getScore()
        );
    }
}
