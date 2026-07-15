package com.example.cafe.order.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private static final Logger log = LoggerFactory.getLogger(OrderEventListener.class);

    @Async
    @EventListener
    public void handleOrderCompleted(OrderCompletedEvent event) {
        log.info("외부 플랫폼 실시간 데이터 전송 완료 - 주문 ID: {}", event.getOrderId());
    }
}
