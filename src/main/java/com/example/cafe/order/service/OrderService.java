package com.example.cafe.order.service;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.order.domain.Order;
import com.example.cafe.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    public Order getOrderAndValidate(Long orderId, Long loginMemberId) {
        Order order = orderRepository.findByIdWithOrderItems(orderId)
                .orElseThrow(() -> new CustomException(ErrorCode.ORDER_NOT_FOUND));

        if (loginMemberId == null || !order.getMemberId().equals(loginMemberId)) {
            throw new CustomException(ErrorCode.ORDER_UNAUTHORIZED_ACCESS);
        }

        return order;
    }
}
