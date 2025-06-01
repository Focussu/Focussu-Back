package com.focussu.backend.analysis;

import com.focussu.backend.auth.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudyFocusAnalysisQueryService {

    private final AuthUtil authUtil;
    private final StudyFocusAnalysisRepository studyFocusAnalysisRepository;

    public List<StudyFocusAnalysisResponse> getMyAnalysisLogs() {
        Long memberId = authUtil.getCurrentMemberId();

        return studyFocusAnalysisRepository.findAllByMemberId(memberId)
                .stream()
                .map(StudyFocusAnalysisResponse::fromEntity)
                .toList();
    }

    public List<StudyFocusAnalysisResponse> getFocusAnalysis(Long ticketId) {
        return studyFocusAnalysisRepository.getStudyFocusAnalysisByTicketNumber(ticketId)
                .stream()
                .map(StudyFocusAnalysisResponse::fromEntity)
                .toList();
    }

}
