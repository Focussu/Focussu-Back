package com.focussu.backend.studyroom.service;

import com.focussu.backend.studyroom.dto.StudyRoomCreateRequest;
import com.focussu.backend.studyroom.dto.StudyRoomCreateResponse;
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
    public StudyRoomCreateResponse createStudyRoom(StudyRoomCreateRequest request) {
        StudyRoom studyRoom = studyRoomRepository.save(request.toEntity());
        return StudyRoomCreateResponse.from(studyRoom);
    }
}
