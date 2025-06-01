package com.focussu.backend.studyparticipation.dto;

import java.time.LocalDate;

public interface StudyParticipationByDateProjection {
    LocalDate getDate();
    Long getTime();
}
