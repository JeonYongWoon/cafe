package com.example.cafe.order.controller;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.global.error.GlobalExceptionHandler;
import com.example.cafe.order.domain.OrderStatus;
import com.example.cafe.order.domain.Temperature;
import com.example.cafe.order.dto.OrderCreateRequest;
import com.example.cafe.order.dto.OrderDetailResponse;
import com.example.cafe.order.dto.OrderResponse;
import com.example.cafe.order.facade.OrderFacade;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Mock
    private OrderFacade orderFacade;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void createOrderSuccess() throws Exception {
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

        OrderResponse.OrderItemResponse orderItemResponse1 = OrderResponse.OrderItemResponse.builder()
                .menuId(1L)
                .temperature(Temperature.ICE)
                .quantity(2)
                .price(4500L)
                .build();

        OrderResponse.OrderItemResponse orderItemResponse2 = OrderResponse.OrderItemResponse.builder()
                .menuId(2L)
                .temperature(Temperature.HOT)
                .quantity(1)
                .price(5000L)
                .build();

        OrderResponse response = OrderResponse.builder()
                .orderId(10023L)
                .totalPrice(14000L)
                .status(OrderStatus.RECEIVED)
                .createdAt(LocalDateTime.of(2026, 7, 13, 16, 20, 0))
                .orderItems(List.of(orderItemResponse1, orderItemResponse2))
                .build();

        when(orderFacade.createOrder(any(OrderCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(10023))
                .andExpect(jsonPath("$.data.totalPrice").value(14000))
                .andExpect(jsonPath("$.data.status").value("RECEIVED"))
                .andExpect(jsonPath("$.data.orderItems").isArray())
                .andExpect(jsonPath("$.data.orderItems[0].menuId").value(1))
                .andExpect(jsonPath("$.data.orderItems[0].temperature").value("ICE"))
                .andExpect(jsonPath("$.data.orderItems[0].quantity").value(2))
                .andExpect(jsonPath("$.data.orderItems[0].price").value(4500))
                .andExpect(jsonPath("$.data.orderItems[1].menuId").value(2))
                .andExpect(jsonPath("$.data.orderItems[1].temperature").value("HOT"))
                .andExpect(jsonPath("$.data.orderItems[1].quantity").value(1))
                .andExpect(jsonPath("$.data.orderItems[1].price").value(5000))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void createOrderFailMemberNotFound() throws Exception {
        OrderCreateRequest.OrderItemRequest item1 = OrderCreateRequest.OrderItemRequest.builder()
                .menuId(1L)
                .temperature(Temperature.ICE)
                .quantity(1)
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .memberId(99L)
                .items(List.of(item1))
                .build();

        when(orderFacade.createOrder(any(OrderCreateRequest.class)))
                .thenThrow(new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("해당 회원을 찾을 수 없습니다."));
    }

    @Test
    void createOrderFailMenuNotAvailable() throws Exception {
        OrderCreateRequest.OrderItemRequest item1 = OrderCreateRequest.OrderItemRequest.builder()
                .menuId(1L)
                .temperature(Temperature.ICE)
                .quantity(1)
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .memberId(1L)
                .items(List.of(item1))
                .build();

        when(orderFacade.createOrder(any(OrderCreateRequest.class)))
                .thenThrow(new CustomException(ErrorCode.MENU_NOT_AVAILABLE));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("MENU_NOT_AVAILABLE"))
                .andExpect(jsonPath("$.error.message").value("존재하지 않는 메뉴이거나 판매 불가능한 상태입니다."));
    }

    @Test
    void createOrderFailInsufficientPoint() throws Exception {
        OrderCreateRequest.OrderItemRequest item1 = OrderCreateRequest.OrderItemRequest.builder()
                .menuId(1L)
                .temperature(Temperature.ICE)
                .quantity(1)
                .build();

        OrderCreateRequest request = OrderCreateRequest.builder()
                .memberId(1L)
                .items(List.of(item1))
                .build();

        when(orderFacade.createOrder(any(OrderCreateRequest.class)))
                .thenThrow(new CustomException(ErrorCode.POINT_INSUFFICIENT));

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("POINT_INSUFFICIENT"))
                .andExpect(jsonPath("$.error.message").value("보유 포인트 잔액이 부족하여 결제에 실패했습니다."));
    }

    @Test
    void getOrderDetailSuccess() throws Exception {
        OrderDetailResponse.OrderItemResponse itemResponse1 = OrderDetailResponse.OrderItemResponse.builder()
                .menuId(1L)
                .name("아메리카노")
                .temperature(Temperature.ICE)
                .quantity(2)
                .price(4500L)
                .build();

        OrderDetailResponse.OrderItemResponse itemResponse2 = OrderDetailResponse.OrderItemResponse.builder()
                .menuId(2L)
                .name("카페라떼")
                .temperature(Temperature.HOT)
                .quantity(1)
                .price(5000L)
                .build();

        OrderDetailResponse response = OrderDetailResponse.builder()
                .orderId(10023L)
                .memberId(1L)
                .totalPrice(14000L)
                .status(OrderStatus.RECEIVED)
                .createdAt(LocalDateTime.of(2026, 7, 13, 16, 20, 0))
                .orderItems(List.of(itemResponse1, itemResponse2))
                .build();

        when(orderFacade.getOrderDetail(10023L, 1L)).thenReturn(response);

        mockMvc.perform(get("/orders/10023")
                        .header("X-Member-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.orderId").value(10023))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.totalPrice").value(14000))
                .andExpect(jsonPath("$.data.status").value("RECEIVED"))
                .andExpect(jsonPath("$.data.orderItems").isArray())
                .andExpect(jsonPath("$.data.orderItems[0].menuId").value(1))
                .andExpect(jsonPath("$.data.orderItems[0].name").value("아메리카노"))
                .andExpect(jsonPath("$.data.orderItems[0].temperature").value("ICE"))
                .andExpect(jsonPath("$.data.orderItems[0].quantity").value(2))
                .andExpect(jsonPath("$.data.orderItems[0].price").value(4500))
                .andExpect(jsonPath("$.data.orderItems[1].menuId").value(2))
                .andExpect(jsonPath("$.data.orderItems[1].name").value("카페라떼"))
                .andExpect(jsonPath("$.data.orderItems[1].temperature").value("HOT"))
                .andExpect(jsonPath("$.data.orderItems[1].quantity").value(1))
                .andExpect(jsonPath("$.data.orderItems[1].price").value(5000))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void getOrderDetailFailOrderNotFound() throws Exception {
        when(orderFacade.getOrderDetail(9999L, 1L))
                .thenThrow(new CustomException(ErrorCode.ORDER_NOT_FOUND));

        mockMvc.perform(get("/orders/9999")
                        .header("X-Member-Id", 1L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("ORDER_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("해당 주문을 찾을 수 없습니다."));
    }

    @Test
    void getOrderDetailFailUnauthorized() throws Exception {
        when(orderFacade.getOrderDetail(10023L, 2L))
                .thenThrow(new CustomException(ErrorCode.ORDER_UNAUTHORIZED_ACCESS));

        mockMvc.perform(get("/orders/10023")
                        .header("X-Member-Id", 2L))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("ORDER_UNAUTHORIZED_ACCESS"))
                .andExpect(jsonPath("$.error.message").value("본인의 주문 내역만 조회할 수 있습니다."));
    }
}
