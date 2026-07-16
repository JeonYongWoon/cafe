package com.example.cafe.order.repository;

import com.example.cafe.order.domain.Order;
import com.example.cafe.order.domain.OrderItem;
import com.example.cafe.order.domain.OrderStatus;
import com.example.cafe.order.domain.Temperature;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class OrderItemRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Test
    void findPopularMenuIdsSuccess() {
        Order order1 = Order.builder()
                .memberId(1L)
                .totalPrice(9000L)
                .status(OrderStatus.COMPLETED)
                .build();
        OrderItem item1 = OrderItem.builder()
                .order(order1)
                .menuId(1L)
                .temperature(Temperature.ICE)
                .quantity(2)
                .price(4500L)
                .build();
        OrderItem item2 = OrderItem.builder()
                .order(order1)
                .menuId(2L)
                .temperature(Temperature.HOT)
                .quantity(1)
                .price(5000L)
                .build();
        order1.addOrderItem(item1);
        order1.addOrderItem(item2);
        orderRepository.save(order1);

        Order order2 = Order.builder()
                .memberId(2L)
                .totalPrice(15000L)
                .status(OrderStatus.COMPLETED)
                .build();
        OrderItem item3 = OrderItem.builder()
                .order(order2)
                .menuId(2L)
                .temperature(Temperature.ICE)
                .quantity(3)
                .price(5000L)
                .build();
        OrderItem item4 = OrderItem.builder()
                .order(order2)
                .menuId(3L)
                .temperature(Temperature.ICE)
                .quantity(5)
                .price(5500L)
                .build();
        order2.addOrderItem(item3);
        order2.addOrderItem(item4);
        orderRepository.save(order2);

        LocalDateTime startDate = LocalDateTime.now().minusDays(7);
        List<PopularMenuProjection> results = orderItemRepository.findPopularMenuIds(startDate, PageRequest.of(0, 3));

        assertThat(results).hasSize(3);

        assertThat(results.get(0).getMenuId()).isEqualTo(3L);
        assertThat(results.get(0).getOrderCount()).isEqualTo(5L);

        assertThat(results.get(1).getMenuId()).isEqualTo(2L);
        assertThat(results.get(1).getOrderCount()).isEqualTo(4L);

        assertThat(results.get(2).getMenuId()).isEqualTo(1L);
        assertThat(results.get(2).getOrderCount()).isEqualTo(2L);
    }
}
