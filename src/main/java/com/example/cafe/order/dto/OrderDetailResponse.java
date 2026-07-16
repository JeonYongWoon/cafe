package com.example.cafe.order.dto;

import com.example.cafe.order.domain.OrderStatus;
import com.example.cafe.order.domain.Temperature;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
public class OrderDetailResponse {
    private final Long orderId;
    private final Long memberId;
    private final Long totalPrice;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItemResponse> orderItems;

    @Builder
    public OrderDetailResponse(Long orderId, Long memberId, Long totalPrice, OrderStatus status, LocalDateTime createdAt, List<OrderItemResponse> orderItems) {
        this.orderId = orderId;
        this.memberId = memberId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
        this.orderItems = orderItems;
    }

    @Getter
    public static class OrderItemResponse {
        private final Long menuId;
        private final String name;
        private final Temperature temperature;
        private final Integer quantity;
        private final Long price;

        @Builder
        public OrderItemResponse(Long menuId, String name, Temperature temperature, Integer quantity, Long price) {
            this.menuId = menuId;
            this.name = name;
            this.temperature = temperature;
            this.quantity = quantity;
            this.price = price;
        }
    }
}
