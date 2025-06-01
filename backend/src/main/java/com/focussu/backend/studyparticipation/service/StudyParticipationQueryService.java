package com.focussu.backend.studyparticipation.service;

import com.focussu.backend.auth.util.AuthUtil;
import com.focussu.backend.studyparticipation.dto.ConcentrationStatsResponse;
import com.focussu.backend.studyparticipation.dto.StudyParticipationByDateProjection;
import com.focussu.backend.studyparticipation.dto.StudyParticipationResponse;
import com.focussu.backend.studyparticipation.repository.StudyParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudyParticipationQueryService {

    private final StudyParticipationRepository repository;
    private final StudyParticipationRepository studyParticipationRepository;

    public StudyParticipationResponse buildStudyTimeResponse(Long memberId, LocalDateTime start, LocalDateTime end) {
        Long seconds = repository.findTotalStudySecondsBetween(memberId, start, end).orElse(0L);
        return StudyParticipationResponse.of(memberId, start, end.minusDays(1), seconds);
    }

    public StudyParticipationResponse buildStudyTimeTotalResponse(Long memberId) {
        Long seconds = repository.findTotalStudySecondsByMemberId(memberId).orElse(0L);
        return StudyParticipationResponse.ofTotal(memberId, seconds);
    }

    public StudyParticipationResponse buildFocusedTimeResponse(Long memberId, LocalDateTime start, LocalDateTime end) {
        Long seconds = repository.findFocusedStudySecondsBetween(memberId, start, end).orElse(0L);
        return StudyParticipationResponse.of(memberId, start, end.minusDays(1), seconds);
    }

    public StudyParticipationResponse buildFocusedTimeTotalResponse(Long memberId) {
        Long seconds = repository.findTotalFocusedStudySeconds(memberId).orElse(0L);
        return StudyParticipationResponse.ofTotal(memberId, seconds);
    }

    public ConcentrationStatsResponse buildConcentrationResponse(Long memberId, LocalDateTime start, LocalDateTime end) {
        Double avg = repository.findAverageConcentrationBetween(memberId, start, end).orElse(0.0);
        return ConcentrationStatsResponse.from(memberId, start.toLocalDate(), end.toLocalDate().minusDays(1), avg);
    }

    public ConcentrationStatsResponse buildConcentrationTotalResponse(Long memberId) {
        Double avg = repository.findAverageConcentration(memberId).orElse(0.0);
        return ConcentrationStatsResponse.from(memberId, null, null, avg);
    }

    public StudyParticipationResponse buildInactiveSecondsResponse(Long memberId) {
        Optional<LocalDateTime> lastExitTime = repository.findLatestEndTimeByMemberId(memberId);
        LocalDateTime now = LocalDateTime.now();
        long seconds = lastExitTime.map(exit -> Duration.between(exit, now).getSeconds()).orElse(0L);
        return StudyParticipationResponse.ofTotal(memberId, seconds);
    }

    public List<StudyParticipationByDateProjection> getParticipationTimeByDate(Long memberId) {
        return studyParticipationRepository.findParticipationTimeGroupedByDate(memberId);
    }

}
