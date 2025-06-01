// StudyParticipationRepository.java
package com.focussu.backend.studyparticipation.repository;

import com.focussu.backend.studyparticipation.dto.StudyParticipationByDateProjection;
import com.focussu.backend.studyparticipation.model.StudyParticipation;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyParticipationRepository extends JpaRepository<StudyParticipation, Long> {

    Optional<StudyParticipation> findTopByMemberIdAndStudyRoomIdAndEndTimeIsNullOrderByStartTimeDesc(Long memberId, Long studyRoomId);

    @Query(value = "SELECT SUM(TIMESTAMPDIFF(SECOND, sp.study_participation_start_time, sp.study_participation_end_time)) FROM study_participation sp WHERE sp.member_id = :memberId AND sp.study_participation_end_time IS NOT NULL", nativeQuery = true)
    Optional<Long> findTotalStudySecondsByMemberId(Long memberId);

    @Query(value = "SELECT SUM(TIMESTAMPDIFF(SECOND, sp.study_participation_start_time, sp.study_participation_end_time)) FROM study_participation sp WHERE sp.member_id = :memberId AND sp.study_participation_start_time >= :start AND sp.study_participation_end_time <= :end", nativeQuery = true)
    Optional<Long> findTotalStudySecondsBetween(Long memberId, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT AVG(sp.study_participation_concentration_score) FROM study_participation sp WHERE sp.member_id = :memberId AND sp.study_participation_start_time >= :start AND sp.study_participation_end_time <= :end AND sp.study_participation_concentration_score IS NOT NULL", nativeQuery = true)
    Optional<Double> findAverageConcentrationBetween(Long memberId, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT AVG(sp.study_participation_concentration_score) FROM study_participation sp WHERE sp.member_id = :memberId AND sp.study_participation_concentration_score IS NOT NULL", nativeQuery = true)
    Optional<Double> findAverageConcentration(Long memberId);

    @Query(value = "SELECT SUM(TIMESTAMPDIFF(SECOND, sp.study_participation_start_time, sp.study_participation_end_time)) FROM study_participation sp WHERE sp.member_id = :memberId AND sp.study_participation_concentration_score IS NOT NULL AND sp.study_participation_start_time >= :start AND sp.study_participation_end_time <= :end", nativeQuery = true)
    Optional<Long> findFocusedStudySecondsBetween(Long memberId, LocalDateTime start, LocalDateTime end);

    @Query(value = "SELECT SUM(TIMESTAMPDIFF(SECOND, sp.study_participation_start_time, sp.study_participation_end_time)) FROM study_participation sp WHERE sp.member_id = :memberId AND sp.study_participation_concentration_score IS NOT NULL", nativeQuery = true)
    Optional<Long> findTotalFocusedStudySeconds(Long memberId);

    @Query(value = "SELECT MAX(sp.study_participation_end_time) FROM study_participation sp WHERE sp.member_id = :memberId", nativeQuery = true)
    Optional<LocalDateTime> findLatestEndTimeByMemberId(Long memberId);

    @Query(
            value = """
        SELECT 
            DATE(study_participation_start_time) AS date,
            SUM(TIMESTAMPDIFF(SECOND, study_participation_start_time, IFNULL(study_participation_end_time, NOW()))) AS time
        FROM study_participation
        WHERE member_id = :memberId
        GROUP BY DATE(study_participation_start_time)
        ORDER BY DATE(study_participation_start_time) DESC
    """,
            nativeQuery = true
    )
    List<StudyParticipationByDateProjection> findParticipationTimeGroupedByDate(@Param("memberId") Long memberId);

}
