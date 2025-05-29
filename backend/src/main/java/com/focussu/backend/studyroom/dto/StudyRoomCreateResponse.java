package com.focussu.backend.studyroom.dto;

import com.focussu.backend.studyroom.model.StudyRoom;

public record StudyRoomCreateResponse(
        Long id,
        String name,
        String description,
        Long maxCapacity,
        String profileImageUrl
) {
    public static StudyRoomCreateResponse from(StudyRoom studyRoom) {
        return new StudyRoomCreateResponse(
                studyRoom.getId(),
                studyRoom.getName(),
                studyRoom.getDescription(),
                studyRoom.getMaxCapacity(),
                studyRoom.getProfileImageUrl()
        );
    }
}
