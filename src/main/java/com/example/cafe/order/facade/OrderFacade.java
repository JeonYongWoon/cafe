package com.example.cafe.order.facade;

import com.example.cafe.member.domain.Member;
import com.example.cafe.member.service.MemberService;
import com.example.cafe.menu.domain.Menu;
import com.example.cafe.menu.service.MenuService;
import com.example.cafe.order.domain.Order;
import com.example.cafe.order.domain.OrderItem;
import com.example.cafe.order.domain.OrderStatus;
import com.example.cafe.order.dto.AdminOrderResponse;
import com.example.cafe.order.dto.OrderCreateRequest;
import com.example.cafe.order.dto.OrderDetailResponse;
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
import java.util.Map;
import java.util.stream.Collectors;

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
        List<OrderItemTempInfo> tempInfos = new ArrayList<>();

        for (OrderCreateRequest.OrderItemRequest itemRequest : request.getItems()) {
            Menu menu = menuService.getMenu(itemRequest.getMenuId());
            menu.validateAvailable();

            totalPrice += menu.getPrice() * itemRequest.getQuantity();
            tempInfos.add(new OrderItemTempInfo(menu, itemRequest));
        }

        member.usePoint(totalPrice);

        Order order = Order.builder()
                .memberId(member.getId())
                .totalPrice(totalPrice)
                .status(OrderStatus.RECEIVED)
                .build();

        for (OrderItemTempInfo temp : tempInfos) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .menuId(temp.menu.getId())
                    .temperature(temp.itemRequest.getTemperature())
                    .quantity(temp.itemRequest.getQuantity())
                    .price(temp.menu.getPrice())
                    .build();
            order.addOrderItem(orderItem);
        }

        Order savedOrder = orderService.saveOrder(order);

        pointService.recordPointUse(member.getId(), savedOrder.getId(), totalPrice);

        eventPublisher.publishEvent(new OrderCompletedEvent(savedOrder.getId()));

        return OrderResponse.from(savedOrder);
    }

    public OrderDetailResponse getOrderDetail(Long orderId, Long loginMemberId) {
        Order order = orderService.getOrderAndValidate(orderId, loginMemberId);

        List<Long> menuIds = order.getOrderItems().stream()
                .map(OrderItem::getMenuId)
                .distinct()
                .toList();

        Map<Long, String> menuNameMap = menuService.getMenus(menuIds).stream()
                .collect(Collectors.toMap(Menu::getId, Menu::getName));

        List<OrderDetailResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderDetailResponse.OrderItemResponse.builder()
                        .menuId(item.getMenuId())
                        .name(menuNameMap.getOrDefault(item.getMenuId(), "알 수 없는 메뉴"))
                        .temperature(item.getTemperature())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .toList();

        return OrderDetailResponse.builder()
                .orderId(order.getId())
                .memberId(order.getMemberId())
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .orderItems(itemResponses)
                .build();
    }

    @Transactional(readOnly = true)
    public List<AdminOrderResponse> getAllOrdersForAdmin() {
        List<Order> orders = orderService.getAllOrders();

        List<Long> memberIds = orders.stream()
                .map(Order::getMemberId)
                .distinct()
                .toList();

        Map<Long, String> memberNameMap = memberService.getMembers(memberIds).stream()
                .collect(Collectors.toMap(Member::getId, Member::getUsername));

        return orders.stream()
                .map(order -> AdminOrderResponse.builder()
                        .orderId(order.getId())
                        .username(memberNameMap.getOrDefault(order.getMemberId(), "알 수 없는 사용자"))
                        .totalPrice(order.getTotalPrice())
                        .status(order.getStatus())
                        .createdAt(order.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    private static class OrderItemTempInfo {
        final Menu menu;
        final OrderCreateRequest.OrderItemRequest itemRequest;

        OrderItemTempInfo(Menu menu, OrderCreateRequest.OrderItemRequest itemRequest) {
            this.menu = menu;
            this.itemRequest = itemRequest;
        }
    }
}
