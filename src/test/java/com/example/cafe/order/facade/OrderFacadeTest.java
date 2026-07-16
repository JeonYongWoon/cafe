package com.example.cafe.order.facade;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.member.domain.Member;
import com.example.cafe.member.service.MemberService;
import com.example.cafe.menu.domain.Menu;
import com.example.cafe.menu.domain.MenuStatus;
import com.example.cafe.menu.service.MenuService;
import com.example.cafe.order.domain.Order;
import com.example.cafe.order.domain.OrderStatus;
import com.example.cafe.order.domain.Temperature;
import com.example.cafe.order.dto.AdminOrderResponse;
import com.example.cafe.order.dto.OrderCreateRequest;
import com.example.cafe.order.dto.OrderDetailResponse;
import com.example.cafe.order.dto.OrderResponse;
import com.example.cafe.order.service.OrderService;
import com.example.cafe.point.service.PointService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.LocalDateTime;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock
    private MemberService memberService;

    @Mock
    private MenuService menuService;

    @Mock
    private PointService pointService;

    @Mock
    private OrderService orderService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private OrderFacade orderFacade;

    @Test
    void createOrderSuccess() {
        Member member = Member.builder()
                .username("user1")
                .password("password")
                .pointBalance(15000L)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        Menu menu1 = Menu.builder()
                .name("아메리카노")
                .price(4500L)
                .status(MenuStatus.AVAILABLE)
                .build();
        ReflectionTestUtils.setField(menu1, "id", 1L);

        Menu menu2 = Menu.builder()
                .name("카페라떼")
                .price(5000L)
                .status(MenuStatus.AVAILABLE)
                .build();
        ReflectionTestUtils.setField(menu2, "id", 2L);

        OrderCreateRequest.OrderItemRequest item1 = OrderCreateRequest.OrderItemRequest.builder()
                .menuId(1L)
                .temperature(Temperature.ICE)
                .quantity(2)
                .build();

        OrderCreateRequest.OrderItemRequest item2 = OrderCreateRequest.OrderItemRequest.builder()
                .menuId(2L)
                .temperature(Temperature.HOT)
                .quantity(1)
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .memberId(1L)
                .items(Arrays.asList(item1, item2))
                .build();

        when(memberService.getMember(1L)).thenReturn(member);
        when(menuService.getMenu(1L)).thenReturn(menu1);
        when(menuService.getMenu(2L)).thenReturn(menu2);

        Order mockSavedOrder = Order.builder()
                .memberId(1L)
                .totalPrice(14000L)
                .status(OrderStatus.RECEIVED)
                .build();
        ReflectionTestUtils.setField(mockSavedOrder, "id", 10023L);

        com.example.cafe.order.domain.OrderItem orderItem1 = com.example.cafe.order.domain.OrderItem.builder()
                .order(mockSavedOrder)
                .menuId(1L)
                .temperature(Temperature.ICE)
                .quantity(2)
                .price(4500L)
                .build();
        com.example.cafe.order.domain.OrderItem orderItem2 = com.example.cafe.order.domain.OrderItem.builder()
                .order(mockSavedOrder)
                .menuId(2L)
                .temperature(Temperature.HOT)
                .quantity(1)
                .price(5000L)
                .build();
        mockSavedOrder.addOrderItem(orderItem1);
        mockSavedOrder.addOrderItem(orderItem2);

        when(orderService.saveOrder(any(Order.class))).thenReturn(mockSavedOrder);

        OrderResponse response = orderFacade.createOrder(request);

        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(10023L);
        assertThat(response.getTotalPrice()).isEqualTo(14000L);
        assertThat(response.getStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(response.getOrderItems()).hasSize(2);
        assertThat(member.getPointBalance()).isEqualTo(1000L);

        verify(pointService, times(1)).recordPointUse(eq(1L), eq(10023L), eq(14000L));
        verify(eventPublisher, times(1)).publishEvent(any(com.example.cafe.order.event.OrderCompletedEvent.class));
    }

    @Test
    void createOrderFailInsufficientPoint() {
        Member member = Member.builder()
                .username("user1")
                .password("password")
                .pointBalance(5000L)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        Menu menu1 = Menu.builder()
                .name("아메리카노")
                .price(4500L)
                .status(MenuStatus.AVAILABLE)
                .build();
        ReflectionTestUtils.setField(menu1, "id", 1L);

        OrderCreateRequest.OrderItemRequest item1 = OrderCreateRequest.OrderItemRequest.builder()
                .menuId(1L)
                .temperature(Temperature.ICE)
                .quantity(2)
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .memberId(1L)
                .items(Arrays.asList(item1))
                .build();

        when(memberService.getMember(1L)).thenReturn(member);
        when(menuService.getMenu(1L)).thenReturn(menu1);

        assertThatThrownBy(() -> orderFacade.createOrder(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POINT_INSUFFICIENT);

        verify(orderService, never()).saveOrder(any());
        verify(pointService, never()).recordPointUse(any(), any(), any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void createOrderFailSoldOutMenu() {
        Member member = Member.builder()
                .username("user1")
                .password("password")
                .pointBalance(15000L)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        Menu menu1 = Menu.builder()
                .name("아메리카노")
                .price(4500L)
                .status(MenuStatus.SOLD_OUT)
                .build();
        ReflectionTestUtils.setField(menu1, "id", 1L);

        OrderCreateRequest.OrderItemRequest item1 = OrderCreateRequest.OrderItemRequest.builder()
                .menuId(1L)
                .temperature(Temperature.ICE)
                .quantity(2)
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .memberId(1L)
                .items(Arrays.asList(item1))
                .build();

        when(memberService.getMember(1L)).thenReturn(member);
        when(menuService.getMenu(1L)).thenReturn(menu1);

        assertThatThrownBy(() -> orderFacade.createOrder(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MENU_NOT_AVAILABLE);

        verify(orderService, never()).saveOrder(any());
    }

    @Test
    void getOrderDetailSuccess() {
        Long orderId = 10023L;
        Long memberId = 1L;

        Order order = Order.builder()
                .memberId(memberId)
                .totalPrice(14000L)
                .status(OrderStatus.RECEIVED)
                .build();
        ReflectionTestUtils.setField(order, "id", orderId);

        com.example.cafe.order.domain.OrderItem orderItem1 = com.example.cafe.order.domain.OrderItem.builder()
                .order(order)
                .menuId(1L)
                .temperature(Temperature.ICE)
                .quantity(2)
                .price(4500L)
                .build();

        com.example.cafe.order.domain.OrderItem orderItem2 = com.example.cafe.order.domain.OrderItem.builder()
                .order(order)
                .menuId(2L)
                .temperature(Temperature.HOT)
                .quantity(1)
                .price(5000L)
                .build();

        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);

        Menu menu1 = Menu.builder()
                .name("아메리카노")
                .price(4500L)
                .status(MenuStatus.AVAILABLE)
                .build();
        ReflectionTestUtils.setField(menu1, "id", 1L);

        Menu menu2 = Menu.builder()
                .name("카페라떼")
                .price(5000L)
                .status(MenuStatus.AVAILABLE)
                .build();
        ReflectionTestUtils.setField(menu2, "id", 2L);

        when(orderService.getOrderAndValidate(orderId, memberId)).thenReturn(order);
        when(menuService.getMenus(List.of(1L, 2L))).thenReturn(List.of(menu1, menu2));

        OrderDetailResponse response = orderFacade.getOrderDetail(orderId, memberId);

        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(orderId);
        assertThat(response.getMemberId()).isEqualTo(memberId);
        assertThat(response.getTotalPrice()).isEqualTo(14000L);
        assertThat(response.getStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(response.getOrderItems()).hasSize(2);
        assertThat(response.getOrderItems().get(0).getName()).isEqualTo("아메리카노");
        assertThat(response.getOrderItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(response.getOrderItems().get(0).getPrice()).isEqualTo(4500L);
        assertThat(response.getOrderItems().get(1).getName()).isEqualTo("카페라떼");
        assertThat(response.getOrderItems().get(1).getQuantity()).isEqualTo(1);
        assertThat(response.getOrderItems().get(1).getPrice()).isEqualTo(5000L);
    }

    @Test
    void getAllOrdersForAdminSuccess() {
        Order order = Order.builder()
                .memberId(1L)
                .totalPrice(14000L)
                .status(OrderStatus.RECEIVED)
                .build();
        ReflectionTestUtils.setField(order, "id", 10023L);
        ReflectionTestUtils.setField(order, "createdAt", LocalDateTime.of(2026, 7, 13, 16, 20, 0));

        Member member = Member.builder()
                .username("user123")
                .password("password")
                .pointBalance(10000L)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        when(orderService.getAllOrders()).thenReturn(List.of(order));
        when(memberService.getMembers(List.of(1L))).thenReturn(List.of(member));

        List<AdminOrderResponse> responses = orderFacade.getAllOrdersForAdmin();

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getOrderId()).isEqualTo(10023L);
        assertThat(responses.get(0).getUsername()).isEqualTo("user123");
        assertThat(responses.get(0).getTotalPrice()).isEqualTo(14000L);
        assertThat(responses.get(0).getStatus()).isEqualTo(OrderStatus.RECEIVED);
        assertThat(responses.get(0).getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 7, 13, 16, 20, 0));
    }
}
