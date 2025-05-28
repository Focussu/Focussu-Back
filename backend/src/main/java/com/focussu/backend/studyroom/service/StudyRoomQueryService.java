package com.focussu.backend.studyroom.service;

import com.focussu.backend.auth.util.AuthUtil;
import com.focussu.backend.common.exception.ErrorCode;
import com.focussu.backend.member.exception.MemberException;
import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import com.focussu.backend.studyroom.dto.StudyRoomCreateResponse;
import com.focussu.backend.studyroom.exception.StudyRoomException;
import com.focussu.backend.studyroom.model.StudyRoom;
import com.focussu.backend.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.focussu.backend.common.exception.ErrorCode.MEMBER_NOT_FOUND;
import static com.focussu.backend.common.exception.ErrorCode.STUDYROOM_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class StudyRoomQueryService {

    private final StudyRoomRepository studyRoomRepository;
    private final MemberRepository memberRepository;
    private final AuthUtil authUtil;

    public StudyRoomCreateResponse getStudyRoom(Long id) {
        StudyRoom studyRoom = studyRoomRepository.findById(id)
                .orElseThrow(() -> new StudyRoomException(STUDYROOM_NOT_FOUND));
        return StudyRoomCreateResponse.from(studyRoom);
    }

    public List<StudyRoomCreateResponse> getStudyRooms() {
        List<StudyRoom> studyRooms = studyRoomRepository.findAll();
        return studyRooms.stream()
                .map(StudyRoomCreateResponse::from)
                .collect(Collectors.toList());
    }

    public List<StudyRoomCreateResponse> getMyStudyRooms() {
        Long currentMemberId = authUtil.getCurrentMemberId();

        Member member = memberRepository.findById(currentMemberId)
                .orElseThrow(() -> new MemberException(MEMBER_NOT_FOUND));

        List<StudyRoom> joinedRooms = member.getJoinedRooms();

        return joinedRooms.stream()
                .map(StudyRoomCreateResponse::from)
                .toList();
    }

}
