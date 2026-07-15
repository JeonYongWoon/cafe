package com.example.cafe.point.service;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.member.domain.Member;
import com.example.cafe.member.repository.MemberRepository;
import com.example.cafe.point.domain.PointHistory;
import com.example.cafe.point.dto.PointChargeRequest;
import com.example.cafe.point.dto.PointChargeResponse;
import com.example.cafe.point.repository.PointHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PointHistoryRepository pointHistoryRepository;

    @InjectMocks
    private PointService pointService;

    @Test
    void chargePointSuccess() {
        PointChargeRequest request = PointChargeRequest.builder()
                .memberId(1L)
                .amount(10000L)
                .build();

        Member member = Member.builder()
                .username("user123")
                .password("password")
                .pointBalance(4500L)
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);

        PointHistory pointHistory = PointHistory.builder()
                .memberId(1L)
                .amount(10000L)
                .type(com.example.cafe.point.domain.PointType.CHARGE)
                .build();
        ReflectionTestUtils.setField(pointHistory, "id", 1001L);

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(pointHistoryRepository.save(any(PointHistory.class))).thenReturn(pointHistory);

        PointChargeResponse response = pointService.chargePoint(request);

        assertThat(response).isNotNull();
        assertThat(response.getMemberId()).isEqualTo(1L);
        assertThat(response.getPointBalance()).isEqualTo(14500L);
        assertThat(response.getPointHistoryId()).isEqualTo(1001L);
    }

    @Test
    void chargePointFailInvalidAmount() {
        PointChargeRequest request = PointChargeRequest.builder()
                .memberId(1L)
                .amount(500L)
                .build();

        assertThatThrownBy(() -> pointService.chargePoint(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.POINT_INVALID_CHARGE_AMOUNT);
    }

    @Test
    void chargePointFailMemberNotFound() {
        PointChargeRequest request = PointChargeRequest.builder()
                .memberId(99L)
                .amount(10000L)
                .build();

        when(memberRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pointService.chargePoint(request))
                .isInstanceOf(CustomException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.MEMBER_NOT_FOUND);
    }
}
