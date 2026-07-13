package com.example.cafe.member.service;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.member.domain.Member;
import com.example.cafe.member.dto.MemberSignupRequest;
import com.example.cafe.member.dto.MemberSignupResponse;
import com.example.cafe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberSignupResponse signup(MemberSignupRequest request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        Member member = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .pointBalance(0L)
                .build();

        Member savedMember = memberRepository.save(member);
        return MemberSignupResponse.from(savedMember);
    }
}
