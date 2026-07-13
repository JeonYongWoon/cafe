package com.example.cafe.member.dto;

import com.example.cafe.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class MemberSignupResponse {
    private final Long memberId;
    private final String username;
    private final LocalDateTime createdAt;

    @Builder
    private MemberSignupResponse(Long memberId, String username, LocalDateTime createdAt) {
        this.memberId = memberId;
        this.username = username;
        this.createdAt = createdAt;
    }

    public static MemberSignupResponse from(Member member) {
        return MemberSignupResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
