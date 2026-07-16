package com.example.cafe.order.service;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.order.domain.Order;
import com.example.cafe.order.domain.OrderStatus;
import com.example.cafe.order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void getOrderAndValidateSuccess() {
        Long orderId = 1L;
        Long memberId = 10L;

        Order order = Order.builder()
                .memberId(memberId)
                .totalPrice(14000L)
                .status(OrderStatus.RECEIVED)
                .build();
        ReflectionTestUtils.setField(order, "id", orderId);

        when(orderRepository.findByIdWithOrderItems(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrderAndValidate(orderId, memberId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(orderId);
        assertThat(result.getMemberId()).isEqualTo(memberId);
    }

    @Test
    void getOrderAndValidateFailOrderNotFound() {
        Long orderId = 1L;
        Long memberId = 10L;

        when(orderRepository.findByIdWithOrderItems(orderId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderAndValidate(orderId, memberId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }

    @Test
    void getOrderAndValidateFailUnauthorized() {
        Long orderId = 1L;
        Long memberId = 10L;
        Long otherMemberId = 20L;

        Order order = Order.builder()
                .memberId(memberId)
                .totalPrice(14000L)
                .status(OrderStatus.RECEIVED)
                .build();
        ReflectionTestUtils.setField(order, "id", orderId);

        when(orderRepository.findByIdWithOrderItems(orderId)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.getOrderAndValidate(orderId, otherMemberId))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ErrorCode.ORDER_UNAUTHORIZED_ACCESS.getMessage());
    }
}
