package com.example.cafe.member.service;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.member.domain.Member;
import com.example.cafe.member.dto.SessionCreateRequest;
import com.example.cafe.member.dto.SessionCreateResponse;
import com.example.cafe.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public SessionCreateResponse login(SessionCreateRequest request) {
        Member member = memberRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        return SessionCreateResponse.from(member);
    }
}
