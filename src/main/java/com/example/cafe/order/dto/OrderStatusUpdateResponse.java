package com.example.cafe.order.dto;

import com.example.cafe.order.domain.Order;
import com.example.cafe.order.domain.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class OrderStatusUpdateResponse {

    private final Long orderId;
    private final OrderStatus status;
    private final LocalDateTime updatedAt;

    @Builder
    public OrderStatusUpdateResponse(Long orderId, OrderStatus status, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    public static OrderStatusUpdateResponse from(Order order) {
        return OrderStatusUpdateResponse.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
