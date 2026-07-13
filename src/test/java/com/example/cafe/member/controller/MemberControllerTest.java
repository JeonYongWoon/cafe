package com.example.cafe.member.controller;

import com.example.cafe.global.dto.ApiResponse;
import com.example.cafe.member.dto.MemberSignupRequest;
import com.example.cafe.member.dto.MemberSignupResponse;
import com.example.cafe.member.service.MemberService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @Mock
    private MemberService memberService;

    @InjectMocks
    private MemberController memberController;

    @Test
    void signupSuccess() {
        MemberSignupRequest request = new MemberSignupRequest("user123", "securepassword");
        MemberSignupResponse response = MemberSignupResponse.builder()
                .memberId(1L)
                .username("user123")
                .build();

        when(memberService.signup(any(MemberSignupRequest.class))).thenReturn(response);

        ResponseEntity<ApiResponse<MemberSignupResponse>> result = memberController.signup(request);

        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().isSuccess()).isTrue();
        assertThat(result.getBody().getData().getUsername()).isEqualTo("user123");
        assertThat(result.getBody().getData().getMemberId()).isEqualTo(1L);
    }
}
