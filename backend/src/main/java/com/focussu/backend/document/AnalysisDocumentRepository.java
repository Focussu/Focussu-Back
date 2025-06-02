package com.focussu.backend.document;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalysisDocumentRepository extends JpaRepository<AnalysisDocument, Long> {
    List<AnalysisDocument> findAllByTicketNumberIn(List<Long> ticketNumbers);

}
