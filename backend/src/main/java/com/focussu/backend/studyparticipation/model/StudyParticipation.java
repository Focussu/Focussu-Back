package com.focussu.backend.studyparticipation.model;

import com.focussu.backend.member.model.Member;
import com.focussu.backend.studyroom.model.StudyRoom;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@Table(name = "study_participation")
@NoArgsConstructor
@AllArgsConstructor
public class StudyParticipation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "study_participation_id")
    private Long id;

    @Column(name = "study_participation_start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "study_participation_end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "study_participation_concentration_score", nullable = true)
    private Double concentrationScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "studyroom_id", nullable = false)
    private StudyRoom studyRoom;
}
