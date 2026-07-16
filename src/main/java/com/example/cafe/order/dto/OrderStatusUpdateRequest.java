package com.example.cafe.order.dto;

import com.example.cafe.order.domain.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderStatusUpdateRequest {

    @NotNull(message = "주문 상태값은 필수 입력 항목입니다.")
    private OrderStatus status;

    public OrderStatusUpdateRequest(OrderStatus status) {
        this.status = status;
    }
}
