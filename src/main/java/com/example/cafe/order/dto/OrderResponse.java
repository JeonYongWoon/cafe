package com.example.cafe.order.dto;

import com.example.cafe.order.domain.Order;
import com.example.cafe.order.domain.OrderStatus;
import com.example.cafe.order.domain.Temperature;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class OrderResponse {
    
    private final Long orderId;
    private final Long totalPrice;
    private final OrderStatus status;
    private final LocalDateTime createdAt;
    private final List<OrderItemResponse> orderItems;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .orderId(order.getId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .orderItems(order.getOrderItems().stream()
                        .map(OrderItemResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }

    @Getter
    @Builder
    public static class OrderItemResponse {
        
        private final Long menuId;
        private final Temperature temperature;
        private final Integer quantity;
        private final Long price;

        public static OrderItemResponse from(com.example.cafe.order.domain.OrderItem orderItem) {
            return OrderItemResponse.builder()
                    .menuId(orderItem.getMenuId())
                    .temperature(orderItem.getTemperature())
                    .quantity(orderItem.getQuantity())
                    .price(orderItem.getPrice())
                    .build();
        }
    }
}
