package com.focussu.backend.member.model;

import com.focussu.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "member_profile_image_url")
    private String profileImageUrl;

    @Builder.Default
    @Column(name = "member_total_study_time")
    private Long totalStudyTime = 0L;
}
