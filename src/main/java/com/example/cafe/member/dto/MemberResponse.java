package com.example.cafe.member.dto;

import com.example.cafe.member.domain.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class MemberResponse {
    private final Long memberId;
    private final String username;
    private final Long pointBalance;

    @Builder
    private MemberResponse(Long memberId, String username, Long pointBalance) {
        this.memberId = memberId;
        this.username = username;
        this.pointBalance = pointBalance;
    }

    public static MemberResponse from(Member member) {
        if (member == null) {
            return null;
        }
        return MemberResponse.builder()
                .memberId(member.getId())
                .username(member.getUsername())
                .pointBalance(member.getPointBalance())
                .build();
    }
}
