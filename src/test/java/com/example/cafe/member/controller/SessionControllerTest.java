package com.example.cafe.member.controller;

import com.example.cafe.member.domain.MemberRole;
import com.example.cafe.member.dto.SessionCreateRequest;
import com.example.cafe.member.dto.SessionCreateResponse;
import com.example.cafe.member.service.SessionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SessionControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private SessionController sessionController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(sessionController).build();
    }

    @Test
    void loginSuccess() throws Exception {
        SessionCreateRequest request = new SessionCreateRequest("user123", "securepassword");
        SessionCreateResponse response = SessionCreateResponse.builder()
                .memberId(1L)
                .username("user123")
                .role(MemberRole.USER)
                .build();

        when(sessionService.login(any(SessionCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("user123"))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.role").value("USER"))
                .andExpect(jsonPath("$.error").isEmpty());
    }
}
