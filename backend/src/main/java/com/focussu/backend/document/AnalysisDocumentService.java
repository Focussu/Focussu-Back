package com.focussu.backend.document;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public List<AnalysisDocumentResponse> getByTicketNumbers(List<Long> ticketNumbers) {
        List<AnalysisDocument> docs = repository.findAllByTicketNumberIn(ticketNumbers);

        return docs.stream()
                .map(doc -> new AnalysisDocumentResponse(doc.getId(), doc.getTicketNumber(), doc.getContent()))
                .toList();
    }

}
