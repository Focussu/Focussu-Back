package com.focussu.backend.studyroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class StudyRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateStudyRoom() throws Exception {
        // POST /studyrooms?name=...
        mockMvc.perform(post("/studyrooms")
                        .param("name", "Study Room 1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Study Room 1"));
    }

    @Test
    public void testGetStudyRoom() throws Exception {
        // 먼저 스터디룸 생성
        String response = mockMvc.perform(post("/studyrooms")
                        .param("name", "Study Room 2"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 생성된 스터디룸의 id 추출
        Map<String, Object> room = objectMapper.readValue(response, Map.class);
        Integer id = (Integer) room.get("id");

        // GET /studyrooms/{id} 호출
        mockMvc.perform(get("/studyrooms/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Study Room 2"));
    }
}
