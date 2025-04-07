package com.focussu.backend.studyroom.service;

import com.focussu.backend.studyroom.model.StudyRoom;
import com.focussu.backend.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class StudyRoomCommandService {

    private final StudyRoomRepository studyRoomRepository;

    @Transactional
    public StudyRoom createStudyRoom(String name) {
        StudyRoom studyRoom = new StudyRoom();
        studyRoom.setName(name);
        // 초기 멤버 구성은 필요에 따라 추가로 처리 가능 (예: 빈 집합 또는 null 처리)
        return studyRoomRepository.save(studyRoom);
    }
}
