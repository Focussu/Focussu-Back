package com.focussu.backend.member.model;

import com.focussu.backend.common.BaseEntity;
import com.focussu.backend.studyparticipation.model.StudyParticipation;
import com.focussu.backend.studyroom.model.StudyRoom;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    // 이름
    @Column(name = "member_name", nullable = false)
    private String name;

    // 이메일 (아이디 역할)
    @Column(name = "member_email", nullable = false, unique = true)
    private String email;

    // 비밀번호
    @Column(name = "member_password", nullable = false)
    private String password;

    // 자기소개
    @Column(name = "member_description")
    private String description;

    // 회원 이미지 주소
    @Column(name = "member_profile_image_url")
    private String profileImageUrl;

    @Builder.Default
    @Column(name = "member_total_study_time")
    private Long totalStudyTime = 0L;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudyParticipation> studyParticipations;

    @ManyToMany(mappedBy = "participants")
    private List<StudyRoom> joinedRooms = new ArrayList<>();
}
