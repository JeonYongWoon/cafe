package com.example.cafe.order.event;

import lombok.Getter;

@Getter
public class OrderCompletedEvent {
    private final Long orderId;

    public OrderCompletedEvent(Long orderId) {
        this.orderId = orderId;
    }
}
