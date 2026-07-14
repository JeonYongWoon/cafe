package com.example.cafe.member.service;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.member.domain.Member;
import com.example.cafe.member.dto.MemberResponse;
import com.example.cafe.member.dto.MemberSignupRequest;
import com.example.cafe.member.dto.MemberSignupResponse;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MemberService memberService;

    @Test
    void signupSuccess() {
        MemberSignupRequest request = new MemberSignupRequest("user123", "securepassword");
        Member member = Member.builder()
                .username("user123")
                .password("encoded_password")
                .pointBalance(0L)
                .build();

        when(memberRepository.existsByUsername("user123")).thenReturn(false);
        when(passwordEncoder.encode("securepassword")).thenReturn("encoded_password");
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        MemberSignupResponse response = memberService.signup(request);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("user123");
    }

    @Test
    void signupFailDuplicateUsername() {
        MemberSignupRequest request = new MemberSignupRequest("user123", "securepassword");

        when(memberRepository.existsByUsername("user123")).thenReturn(true);

        assertThatThrownBy(() -> memberService.signup(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_USERNAME);
    }

    @Test
    void getMemberDetailSuccess() {
        Member member = Member.builder()
                .username("user123")
                .password("encoded_password")
                .pointBalance(10000L)
                .build();
        org.springframework.test.util.ReflectionTestUtils.setField(member, "id", 1L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));

        MemberResponse response = memberService.getMemberDetail(1L);

        assertThat(response).isNotNull();
        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getUsername()).isEqualTo("user123");
        assertThat(response.getPointBalance()).isEqualTo(10000L);
    }

    @Test
    void getMemberDetailFailNotFound() {
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> memberService.getMemberDetail(1L))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }
}
