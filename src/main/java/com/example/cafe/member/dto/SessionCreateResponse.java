package com.example.cafe.member.dto;

import com.example.cafe.member.domain.Member;
import com.example.cafe.member.domain.MemberRole;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SessionCreateResponse implements java.io.Serializable {

    private static final long serialVersionUID = 1L;

    private Long memberId;
    private String username;
    private MemberRole role;

    @Builder
    public SessionCreateResponse(Long memberId, String username, MemberRole role) {
        this.memberId = memberId;
        this.username = username;
        this.role = role;
    }

    public static SessionCreateResponse from(Member member) {
        return SessionCreateResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .role(member.getRole())
                .build();
    }
}
