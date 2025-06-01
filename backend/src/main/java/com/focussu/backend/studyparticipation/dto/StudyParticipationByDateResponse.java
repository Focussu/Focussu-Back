package com.focussu.backend.studyparticipation.dto;

import java.time.LocalDate;

public record StudyParticipationByDateResponse(
        LocalDate date,
        long time
) {}
