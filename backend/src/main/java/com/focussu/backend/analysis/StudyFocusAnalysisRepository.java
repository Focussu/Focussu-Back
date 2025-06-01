package com.focussu.backend.analysis;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudyFocusAnalysisRepository extends JpaRepository<StudyFocusAnalysis, Long> {
    @Query("SELECT AVG(s.score) FROM StudyFocusAnalysis s WHERE s.ticketNumber = :ticketId")
    Double calculateAverageScoreByTicketId(@Param("ticketId") Long ticketId);

    List<StudyFocusAnalysis> getStudyFocusAnalysisByTicketNumber(Long ticketNumber);

    @Query("""
        SELECT sfa
        FROM StudyFocusAnalysis sfa
        WHERE sfa.ticketNumber IN (
            SELECT sp.id
            FROM StudyParticipation sp
            WHERE sp.member.id = :memberId
        )
    """)
    List<StudyFocusAnalysis> findAllByMemberId(@Param("memberId") Long memberId);
}
