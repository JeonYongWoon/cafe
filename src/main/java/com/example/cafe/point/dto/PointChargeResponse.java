package com.example.cafe.point.dto;

import com.example.cafe.member.domain.Member;
import com.example.cafe.point.domain.PointHistory;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PointChargeResponse {
    private final Long memberId;
    private final Long pointBalance;
    private final Long pointHistoryId;

    @Builder
    private PointChargeResponse(Long memberId, Long pointBalance, Long pointHistoryId) {
        this.memberId = memberId;
        this.pointBalance = pointBalance;
        this.pointHistoryId = pointHistoryId;
    }

    public static PointChargeResponse of(Member member, PointHistory pointHistory) {
        if (member == null || pointHistory == null) {
            return null;
        }
        return PointChargeResponse.builder()
                .memberId(member.getId())
                .pointBalance(member.getPointBalance())
                .pointHistoryId(pointHistory.getId())
                .build();
    }
}
