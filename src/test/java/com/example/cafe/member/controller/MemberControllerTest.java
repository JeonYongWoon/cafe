package com.example.cafe.member.controller;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.global.error.GlobalExceptionHandler;
import com.example.cafe.member.dto.MemberResponse;
import com.example.cafe.member.dto.MemberSignupRequest;
import com.example.cafe.member.dto.MemberSignupResponse;
import com.example.cafe.member.service.MemberService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void signupSuccess() throws Exception {
        MemberSignupRequest request = new MemberSignupRequest("user123", "securepassword");
        MemberSignupResponse response = MemberSignupResponse.builder()
                .memberId(1L)
                .username("user123")
                .build();

        when(memberService.signup(any(MemberSignupRequest.class))).thenReturn(response);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("user123"))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void getMemberDetailSuccess() throws Exception {
        MemberResponse response = MemberResponse.builder()
                .memberId(1L)
                .username("user123")
                .pointBalance(10000L)
                .build();

        when(memberService.getMemberDetail(1L)).thenReturn(response);

        mockMvc.perform(get("/members/{memberId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.username").value("user123"))
                .andExpect(jsonPath("$.data.pointBalance").value(10000))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void getMemberDetailFailNotFound() throws Exception {
        when(memberService.getMemberDetail(1L))
                .thenThrow(new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        mockMvc.perform(get("/members/{memberId}", 1L))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("해당 회원을 찾을 수 없습니다."));
    }
}
