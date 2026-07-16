package com.example.cafe.order.dto;

import com.example.cafe.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class AdminOrderResponse {
    private final Long orderId;
    private final String username;
    private final Long totalPrice;
    private final OrderStatus status;
    private final LocalDateTime createdAt;

    @Builder
    public AdminOrderResponse(Long orderId, String username, Long totalPrice, OrderStatus status, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.username = username;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }
}
