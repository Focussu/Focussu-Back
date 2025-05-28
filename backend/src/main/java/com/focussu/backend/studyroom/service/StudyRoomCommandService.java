package com.focussu.backend.studyroom.service;

import com.focussu.backend.auth.util.AuthUtil;
import com.focussu.backend.member.exception.MemberException;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import com.focussu.backend.studyroom.dto.StudyRoomCreateRequest;
import com.focussu.backend.studyroom.dto.StudyRoomCreateResponse;
import com.focussu.backend.studyroom.dto.StudyRoomJoinResponse;
import com.focussu.backend.studyroom.exception.StudyRoomException;
import com.focussu.backend.studyroom.model.StudyRoom;
import com.focussu.backend.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.focussu.backend.common.exception.ErrorCode.*;

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
                .orElseThrow(() -> new StudyRoomException(STUDYROOM_NOT_FOUND));

        if (studyRoom.getParticipants().size() >= studyRoom.getMaxCapacity()) {
            throw new StudyRoomException(STUDYROOM_EXCEEDS_CAPACITY);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        // 이미 참여한 경우 중복 방지
        if (studyRoom.getParticipants().contains(member)) {
            throw new StudyRoomException(STUDYROOM_ALREADY_JOINED);
        }

        // 양방향 관계 설정
        studyRoom.getParticipants().add(member);
        member.getJoinedRooms().add(studyRoom);

        return StudyRoomJoinResponse.from(studyRoom, member);
    }
}
