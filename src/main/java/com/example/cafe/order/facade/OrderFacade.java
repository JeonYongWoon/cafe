package com.example.cafe.order.facade;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.member.domain.Member;
import com.example.cafe.member.service.MemberService;
import com.example.cafe.menu.domain.Menu;
import com.example.cafe.menu.domain.MenuStatus;
import com.example.cafe.menu.service.MenuService;
import com.example.cafe.order.domain.Order;
import com.example.cafe.order.domain.OrderItem;
import com.example.cafe.order.domain.OrderStatus;
import com.example.cafe.order.dto.OrderCreateRequest;
import com.example.cafe.order.dto.OrderResponse;
import com.example.cafe.order.event.OrderCompletedEvent;
import com.example.cafe.order.service.OrderService;
import com.example.cafe.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final MemberService memberService;
    private final MenuService menuService;
    private final PointService pointService;
    private final OrderService orderService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        Member member = memberService.getMember(request.getMemberId());

        long totalPrice = 0;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getItems()) {
            Menu menu = menuService.getMenu(itemRequest.getMenuId());

            if (menu.getStatus() == MenuStatus.SOLD_OUT || menu.getStatus() == MenuStatus.DISCONTINUED) {
                throw new CustomException(ErrorCode.MENU_NOT_AVAILABLE);
            }

            OrderItem orderItem = OrderItem.builder()
                    .menuId(menu.getId())
                    .temperature(itemRequest.getTemperature())
                    .quantity(itemRequest.getQuantity())
                    .price(menu.getPrice())
                    .build();

            orderItems.add(orderItem);
            totalPrice += menu.getPrice() * itemRequest.getQuantity();
        }

        member.usePoint(totalPrice);

        Order order = Order.builder()
                .memberId(member.getId())
                .totalPrice(totalPrice)
                .status(OrderStatus.RECEIVED)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        Order savedOrder = orderService.saveOrder(order);

        pointService.recordPointUse(member.getId(), savedOrder.getId(), totalPrice);

        eventPublisher.publishEvent(new OrderCompletedEvent(savedOrder.getId()));

        return OrderResponse.from(savedOrder);
    }
}
