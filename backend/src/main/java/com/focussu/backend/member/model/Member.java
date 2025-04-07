package com.focussu.backend.member.model;

import com.focussu.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 이름
    @Column(nullable = false)
    private String name;

    // 이메일 (아이디 역할)
    @Column(nullable = false, unique = true)
    private String email;

    // 비밀번호
    @Column(nullable = false)
    private String password;

    @Builder.Default
    @Column(name = "total_concentration_time")
    private Long totalConcentrationTime = 0L;

    @Builder.Default
    @Column(name = "weekly_concentration_time")
    private Long weeklyConcentrationTime = 0L;

    @Builder.Default
    @Column(name = "daily_concentration_time")
    private Long dailyConcentrationTime = 0L;
}
