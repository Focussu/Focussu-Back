package com.focussu.backend.document;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnalysisDocumentRepository extends JpaRepository<AnalysisDocument, Long> {
    Optional<AnalysisDocument> findByTicketNumber(Long ticketNumber);
}
