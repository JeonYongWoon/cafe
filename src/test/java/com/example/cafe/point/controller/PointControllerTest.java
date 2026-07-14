package com.example.cafe.point.controller;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.global.error.GlobalExceptionHandler;
import com.example.cafe.point.dto.PointChargeRequest;
import com.example.cafe.point.dto.PointChargeResponse;
import com.example.cafe.point.service.PointService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PointControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private PointService pointService;

    @InjectMocks
    private PointController pointController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(pointController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void chargePointSuccess() throws Exception {
        PointChargeRequest request = PointChargeRequest.builder()
                .memberId(1L)
                .amount(10000L)
                .build();

        PointChargeResponse response = PointChargeResponse.builder()
                .memberId(1L)
                .pointBalance(14500L)
                .pointHistoryId(1001L)
                .build();

        when(pointService.chargePoint(any(PointChargeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.memberId").value(1))
                .andExpect(jsonPath("$.data.pointBalance").value(14500))
                .andExpect(jsonPath("$.data.pointHistoryId").value(1001))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    void chargePointFailInvalidAmount() throws Exception {
        PointChargeRequest request = PointChargeRequest.builder()
                .memberId(1L)
                .amount(500L)
                .build();

        mockMvc.perform(post("/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("INVALID_CHARGE_AMOUNT"))
                .andExpect(jsonPath("$.error.message").value("충전 금액은 최소 1,000원 이상이어야 합니다."));
    }

    @Test
    void chargePointFailMemberNotFound() throws Exception {
        PointChargeRequest request = PointChargeRequest.builder()
                .memberId(99L)
                .amount(10000L)
                .build();

        when(pointService.chargePoint(any(PointChargeRequest.class)))
                .thenThrow(new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        mockMvc.perform(post("/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.data").isEmpty())
                .andExpect(jsonPath("$.error.code").value("MEMBER_NOT_FOUND"))
                .andExpect(jsonPath("$.error.message").value("해당 회원을 찾을 수 없습니다."));
    }
}
