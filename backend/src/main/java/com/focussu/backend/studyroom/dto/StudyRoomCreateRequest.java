package com.focussu.backend.studyroom.dto;

import com.focussu.backend.studyroom.model.StudyRoom;

public record StudyRoomCreateRequest(String name, long maxCapacity, String description, String profileImageUrl) {
    public StudyRoom toEntity() {
        return StudyRoom.builder()
                .name(name)
                .description(description)
                .maxCapacity(maxCapacity)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
