package com.focussu.backend.studyroom.model;

import com.focussu.backend.common.BaseEntity;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.studyparticipation.model.StudyParticipation;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "studyroom")
@NoArgsConstructor
@AllArgsConstructor
public class StudyRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studyroom_id")
    private Long id;

    @Column(name = "studyroom_name", nullable = false)
    private String name;

    @Column(name = "studyroom_description", nullable = false)
    private String description;

    @Column(name = "studyroom_max_capacity", nullable = false)
    private Long maxCapacity;

    @Column(name = "studyroom_profile_image_url")
    private String profileImageUrl;

    @Column(name = "studyroom_is_active")
    @Builder.Default
    private Boolean isActive = true;

    @OneToMany(mappedBy = "studyRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyParticipation> studyParticipations;

    @ManyToMany
    @JoinTable(
            name = "studyroom_participants",
            joinColumns = @JoinColumn(name = "studyroom_id"),
            inverseJoinColumns = @JoinColumn(name = "member_id")
    )
    private List<Member> participants = new ArrayList<>();

}
