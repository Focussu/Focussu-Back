package com.focussu.backend.studyroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.focussu.backend.studyroom.dto.StudyRoomCreateRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class StudyRoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testCreateStudyRoom() throws Exception {
        StudyRoomCreateRequest request = new StudyRoomCreateRequest(
                "스터디룸 A", 10L, "설명 A", "http://image.url/a.jpg"
        );

        mockMvc.perform(post("/studyrooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("스터디룸 A"))
                .andExpect(jsonPath("$.maxCapacity").value(10))
                .andExpect(jsonPath("$.description").value("설명 A"));
    }

    @Test
    public void testGetStudyRoom() throws Exception {
        StudyRoomCreateRequest request = new StudyRoomCreateRequest(
                "스터디룸 B", 20L, "설명 B", "http://image.url/b.jpg"
        );

        String response = mockMvc.perform(post("/studyrooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Map<String, Object> room = objectMapper.readValue(response, Map.class);
        Integer id = (Integer) room.get("id");

        mockMvc.perform(get("/studyrooms/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("스터디룸 B"))
                .andExpect(jsonPath("$.description").value("설명 B"))
                .andExpect(jsonPath("$.maxCapacity").value(20));
    }
}
