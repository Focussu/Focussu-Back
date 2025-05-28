package com.focussu.backend.studyroom.service;

import com.focussu.backend.auth.util.AuthUtil;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import com.focussu.backend.studyroom.dto.StudyRoomCreateRequest;
import com.focussu.backend.studyroom.dto.StudyRoomCreateResponse;
import com.focussu.backend.studyroom.dto.StudyRoomJoinResponse;
import com.focussu.backend.studyroom.model.StudyRoom;
import com.focussu.backend.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StudyRoomCommandService {

    private final StudyRoomRepository studyRoomRepository;
    private final MemberRepository memberRepository;
    private final AuthUtil authUtil;

    @Transactional
    public StudyRoomCreateResponse createStudyRoom(StudyRoomCreateRequest request) {
        StudyRoom studyRoom = studyRoomRepository.save(request.toEntity());
        return StudyRoomCreateResponse.from(studyRoom);
    }

    @Transactional
    public StudyRoomJoinResponse joinStudyRoom(Long studyRoomId) {
        Long memberId = authUtil.getCurrentMemberId();

        StudyRoom studyRoom = studyRoomRepository.findById(studyRoomId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 스터디룸입니다."));

        if (studyRoom.getParticipants().size() >= studyRoom.getMaxCapacity()) {
            throw new IllegalStateException("스터디룸 정원이 초과되었습니다.");
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 이미 참여한 경우 중복 방지
        if (studyRoom.getParticipants().contains(member)) {
            throw new IllegalStateException("이미 참여한 스터디룸입니다.");
        }

        // 양방향 관계 설정
        studyRoom.getParticipants().add(member);
        member.getJoinedRooms().add(studyRoom);

        return StudyRoomJoinResponse.from(studyRoom, member);
    }
}
