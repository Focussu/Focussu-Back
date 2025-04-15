package com.focussu.backend.studyparticipation;

import com.focussu.backend.config.SecurityConfig;
import com.focussu.backend.studyparticipation.controller.StudyParticipationController;
import com.focussu.backend.studyparticipation.service.StudyParticipationCommandService;
import com.focussu.backend.studyparticipation.service.StudyParticipationQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;

import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudyParticipationController.class)
@Import(SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class StudyParticipationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StudyParticipationCommandService commandService;

    @MockBean
    private StudyParticipationQueryService queryService;

    @Test
    @DisplayName("스터디룸 참여 요청 - 성공")
    void joinStudyRoom() throws Exception {
        // given
        Long roomId = 1L;
        String userId = "user123";
        doNothing().when(commandService).addParticipant(roomId, userId);

        // when + then
        mockMvc.perform(post("/api/studyrooms/{roomId}/participants/{userId}", roomId, userId))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("스터디룸 퇴장 요청 - 성공")
    void leaveStudyRoom() throws Exception {
        // given
        Long roomId = 1L;
        String userId = "user123";
        doNothing().when(commandService).removeParticipant(roomId, userId);

        // when + then
        mockMvc.perform(delete("/api/studyrooms/{roomId}/participants/{userId}", roomId, userId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("스터디룸 참가자 조회 - 성공")
    void getParticipants() throws Exception {
        // given
        Long roomId = 1L;
        Set<String> dummyParticipants = Set.of("user1", "user2");
        Mockito.when(queryService.getParticipants(roomId)).thenReturn(dummyParticipants);

        // when + then
        mockMvc.perform(get("/api/studyrooms/{roomId}/participants", roomId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(org.hamcrest.Matchers.containsInAnyOrder("user1", "user2")));

    }
}
