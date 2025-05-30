package com.focussu.backend.analysis;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StudyFocusAnalysisRepository extends JpaRepository<StudyFocusAnalysis, Long> {
    @Query("SELECT AVG(s.score) FROM StudyFocusAnalysis s WHERE s.ticketNumber = :ticketId")
    Double calculateAverageScoreByTicketId(@Param("ticketId") Long ticketId);

}
