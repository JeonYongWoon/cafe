package com.example.cafe.member.service;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.member.domain.Member;
import com.example.cafe.member.dto.SessionCreateRequest;
import com.example.cafe.member.dto.SessionCreateResponse;
import com.example.cafe.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private SessionService sessionService;

    @Test
    void loginSuccess() {
        SessionCreateRequest request = new SessionCreateRequest("user123", "securepassword");
        Member member = Member.builder()
                .username("user123")
                .password("encoded_password")
                .build();

        when(memberRepository.findByUsername("user123")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("securepassword", "encoded_password")).thenReturn(true);

        SessionCreateResponse response = sessionService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("user123");
    }

    @Test
    void loginFailUserNotFound() {
        SessionCreateRequest request = new SessionCreateRequest("nonexist", "password");

        when(memberRepository.findByUsername("nonexist")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> sessionService.login(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);
    }

    @Test
    void loginFailInvalidPassword() {
        SessionCreateRequest request = new SessionCreateRequest("user123", "wrongpassword");
        Member member = Member.builder()
                .username("user123")
                .password("encoded_password")
                .build();

        when(memberRepository.findByUsername("user123")).thenReturn(Optional.of(member));
        when(passwordEncoder.matches("wrongpassword", "encoded_password")).thenReturn(false);

        assertThatThrownBy(() -> sessionService.login(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);
    }
}
