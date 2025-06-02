package com.focussu.backend.document;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AnalysisDocumentService {

    private final AnalysisDocumentRepository repository;

    @Transactional
    public AnalysisDocumentResponse create(AnalysisDocumentRequest request) {
        AnalysisDocument saved = repository.save(
                AnalysisDocument.builder()
                        .ticketNumber(request.ticketNumber())
                        .content(request.content())
                        .build()
        );

        return new AnalysisDocumentResponse(saved.getId(), saved.getTicketNumber(), saved.getContent());
    }

    @Transactional(readOnly = true)
    public AnalysisDocumentResponse getByTicketNumber(Long ticketNumber) {
        AnalysisDocument doc = repository.findByTicketNumber(ticketNumber)
                .orElseThrow(() -> new IllegalArgumentException("해당 티켓 번호의 문서를 찾을 수 없습니다."));

        return new AnalysisDocumentResponse(doc.getId(), doc.getTicketNumber(), doc.getContent());
    }
}
