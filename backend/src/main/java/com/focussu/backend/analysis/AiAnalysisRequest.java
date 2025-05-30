package com.focussu.backend.analysis;

import java.time.LocalDateTime;

public record AiAnalysisRequest(
        Long ticketNumber,
        LocalDateTime startTime,
        LocalDateTime endTime,
        Double score
) {
}
