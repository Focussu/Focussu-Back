//package com.focussu.backend.member.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.focussu.backend.member.dto.MemberCreateRequest;
//import com.focussu.backend.member.model.Member;
//import com.focussu.backend.member.repository.MemberRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc(addFilters = false)
//class MemberControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private MemberRepository memberRepository;
//
//    @BeforeEach
//    void setUp() {
//        memberRepository.deleteAll();
//    }
//
//    @Test
//    void createMember_and_getMember_success() throws Exception {
//        // given
//        MemberCreateRequest request = new MemberCreateRequest("정태", "test@email.com", "pass123");
//
//        // when: 회원 생성
//        String response = mockMvc.perform(post("/api/members")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("정태"))
//                .andExpect(jsonPath("$.email").value("test@email.com"))
//                .andReturn().getResponse().getContentAsString();
//
//        Member created = memberRepository.findAll().get(0);
//        assertThat(created.getName()).isEqualTo("정태");
//
//        // when: 회원 조회
//        mockMvc.perform(get("/api/members/" + created.getId()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.name").value("정태"))
//                .andExpect(jsonPath("$.email").value("test@email.com"));
//    }
//}
