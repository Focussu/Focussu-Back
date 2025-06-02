package com.focussu.backend.document;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "analysis_document")
public class AnalysisDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long id;

    @Column(name = "document_ticket_number", nullable = false)
    private Long ticketNumber;

    @Column(name = "document_content", columnDefinition = "TEXT", nullable = false)
    private String content;
}
