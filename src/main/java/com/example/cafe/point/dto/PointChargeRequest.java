package com.example.cafe.point.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointChargeRequest {

    @NotNull(message = "SYSTEM_INVALID_INPUT_VALUE:회원 식별값은 필수입니다.")
    private Long memberId;

    @NotNull(message = "SYSTEM_INVALID_INPUT_VALUE:충전 금액은 필수입니다.")
    @Min(value = 1000, message = "POINT_INVALID_CHARGE_AMOUNT:충전 금액은 최소 1,000원 이상이어야 합니다.")
    private Long amount;

    @Builder
    public PointChargeRequest(Long memberId, Long amount) {
        this.memberId = memberId;
        this.amount = amount;
    }
}
