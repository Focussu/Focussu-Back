package com.focussu.backend.member.model;

import com.focussu.backend.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@Table(name = "friendship", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"requester_id", "receiver_id"})
})
@NoArgsConstructor
@AllArgsConstructor
public class FriendShip extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "friendship_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", referencedColumnName = "member_id", nullable = false)
    private Member requester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "member_id", nullable = false)
    private Member receiver;

    // 친구 수락 여부
    @Column(name = "is_accepted", nullable = false)
    private boolean isAccepted;
}
