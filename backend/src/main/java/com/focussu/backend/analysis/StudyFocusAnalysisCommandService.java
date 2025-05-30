package com.focussu.backend.analysis;

import com.focussu.backend.studyparticipation.repository.StudyParticipationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StudyFocusAnalysisCommandService {

    private final StudyFocusAnalysisRepository studyFocusAnalysisRepository;
    private final StudyParticipationRepository studyParticipationRepository;

    @Transactional
    public void saveAnalysis(AiAnalysisRequest dto) {
        StudyFocusAnalysis analysis = StudyFocusAnalysis.builder()
                .ticketNumber(dto.ticketNumber())
                .startTime(dto.startTime())
                .endTime(dto.endTime())
                .score(dto.score())
                .build();

        studyFocusAnalysisRepository.save(analysis);
    }

    @Transactional
    public void updateFocusAnalysis(Long ticketId) {
        // 1. 해당 ticketId의 모든 점수 평균 구하기
        Double averageScore = studyFocusAnalysisRepository.calculateAverageScoreByTicketId(ticketId);

        if (averageScore == null) {
            throw new IllegalStateException("해당 ticketId에 대한 분석 점수가 존재하지 않습니다: " + ticketId);
        }

        // 2. StudyParticipation 엔티티 업데이트
        studyParticipationRepository.findById(ticketId).ifPresentOrElse(participation -> {
            participation.setConcentrationScore(averageScore);
        }, () -> {
            throw new IllegalArgumentException("해당 ticketId에 대한 StudyParticipation이 존재하지 않습니다: " + ticketId);
        });
    }

}
