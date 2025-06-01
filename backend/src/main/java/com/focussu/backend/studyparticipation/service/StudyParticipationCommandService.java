package com.focussu.backend.studyparticipation.service;

import com.focussu.backend.member.model.Member;
import com.focussu.backend.member.repository.MemberRepository;
import com.focussu.backend.studyparticipation.model.StudyParticipation;
import com.focussu.backend.studyparticipation.repository.StudyParticipationRepository;
import com.focussu.backend.studyroom.model.StudyRoom;
import com.focussu.backend.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StudyParticipationCommandService {

    private final StudyParticipationRepository studyParticipationRepository;
    private final MemberRepository memberRepository;
    private final StudyRoomRepository studyRoomRepository;

    public Long createParticipation(Long memberId, Long roomId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("Invalid memberId"));
        StudyRoom studyRoom = studyRoomRepository.findById(roomId).orElseThrow(() -> new IllegalArgumentException("Invalid studyRoomId"));

        StudyParticipation participation = StudyParticipation.builder()
                .member(member)
                .studyRoom(studyRoom)
                .startTime(LocalDateTime.now())
                .build();

        StudyParticipation save = studyParticipationRepository.save(participation);

        return save.getId();
    }

    public void endParticipation(Long memberId, Long roomId) {
        StudyParticipation participation = studyParticipationRepository
                .findTopByMemberIdAndStudyRoomIdAndEndTimeIsNullOrderByStartTimeDesc(memberId, roomId)
                .orElseThrow(() -> new IllegalStateException("No ongoing participation found"));

        participation.setEndTime(LocalDateTime.now());
        studyParticipationRepository.save(participation);
    }
}