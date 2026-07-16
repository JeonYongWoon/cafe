package com.example.cafe.order.controller;

import com.example.cafe.global.error.GlobalExceptionHandler;
import com.example.cafe.order.domain.OrderStatus;
import com.example.cafe.order.dto.AdminOrderResponse;
import com.example.cafe.order.facade.OrderFacade;
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
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminOrderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private OrderFacade orderFacade;

    @InjectMocks
    private AdminOrderController adminOrderController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminOrderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllOrdersSuccess() throws Exception {
        AdminOrderResponse orderResponse = AdminOrderResponse.builder()
                .orderId(10023L)
                .username("user123")
                .totalPrice(14000L)
                .status(OrderStatus.RECEIVED)
                .createdAt(LocalDateTime.of(2026, 7, 13, 16, 20, 0))
                .build();

        when(orderFacade.getAllOrdersForAdmin()).thenReturn(List.of(orderResponse));

        mockMvc.perform(get("/admin/orders")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].orderId").value(10023))
                .andExpect(jsonPath("$.data[0].username").value("user123"))
                .andExpect(jsonPath("$.data[0].totalPrice").value(14000))
                .andExpect(jsonPath("$.data[0].status").value("RECEIVED"))
                .andExpect(jsonPath("$.data[0].createdAt").value("2026-07-13T16:20:00"))
                .andExpect(jsonPath("$.error").isEmpty());
    }
}
