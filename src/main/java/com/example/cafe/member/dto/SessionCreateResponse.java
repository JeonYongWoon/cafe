package com.example.cafe.member.dto;

import com.example.cafe.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionCreateResponse {

    private Long memberId;
    private String username;

    @Builder
    public SessionCreateResponse(Long memberId, String username) {
        this.memberId = memberId;
        this.username = username;
    }

    public static SessionCreateResponse from(Member member) {
        return SessionCreateResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .build();
    }
}
