package com.focussu.backend.studyroom.dto;

import com.focussu.backend.member.model.Member;
import com.focussu.backend.studyroom.model.StudyRoom;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "스터디룸 참가 응답 DTO")
public record StudyRoomJoinResponse(
        @Schema(description = "스터디룸 ID", example = "1")
        Long studyRoomId,

        @Schema(description = "참가한 멤버 ID", example = "42")
        Long memberId
) {
    public static StudyRoomJoinResponse from(StudyRoom studyRoom, Member member) {
        return new StudyRoomJoinResponse(
                studyRoom.getId(),
                member.getId()
        );
    }
}
