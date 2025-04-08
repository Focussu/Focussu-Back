package com.focussu.backend.studyroom.service;

import com.focussu.backend.studyroom.dto.StudyRoomCreateResponse;
import com.focussu.backend.studyroom.model.StudyRoom;
import com.focussu.backend.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StudyRoomQueryService {

    private final StudyRoomRepository studyRoomRepository;

    public StudyRoomCreateResponse getStudyRoom(Long id) {
        StudyRoom studyRoom = studyRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        return StudyRoomCreateResponse.from(studyRoom);
    }
}
