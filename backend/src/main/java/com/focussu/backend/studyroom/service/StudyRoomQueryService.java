package com.focussu.backend.studyroom.service;

import com.focussu.backend.studyroom.model.StudyRoom;
import com.focussu.backend.studyroom.repository.StudyRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StudyRoomQueryService {

    private final StudyRoomRepository studyRoomRepository;

    public StudyRoom getStudyRoomById(Long id) {
        return studyRoomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("StudyRoom not found"));
    }
}
