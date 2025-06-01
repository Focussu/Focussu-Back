package com.focussu.backend.analysis;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyFocusAnalysisQueryService {

    private final StudyFocusAnalysisRepository studyFocusAnalysisRepository;

    public List<StudyFocusAnalysisResponse> getFocusAnalysis(Long ticketId) {
        return studyFocusAnalysisRepository.getStudyFocusAnalysisByTicketNumber(ticketId)
                .stream()
                .map(StudyFocusAnalysisResponse::fromEntity)
                .toList();
    }

}
