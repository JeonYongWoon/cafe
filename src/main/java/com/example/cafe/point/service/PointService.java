package com.example.cafe.point.service;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.member.domain.Member;
import com.example.cafe.member.repository.MemberRepository;
import com.example.cafe.point.domain.PointHistory;
import com.example.cafe.point.domain.PointType;
import com.example.cafe.point.dto.PointChargeRequest;
import com.example.cafe.point.dto.PointChargeResponse;
import com.example.cafe.point.repository.PointHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointService {

    private final MemberRepository memberRepository;
    private final PointHistoryRepository pointHistoryRepository;

    @Transactional
    public PointChargeResponse chargePoint(PointChargeRequest request) {
        if (request.getAmount() == null || request.getAmount() < 1000) {
            throw new CustomException(ErrorCode.INVALID_CHARGE_AMOUNT);
        }

        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.chargePoint(request.getAmount());

        PointHistory pointHistory = PointHistory.builder()
                .memberId(member.getId())
                .amount(request.getAmount())
                .type(PointType.CHARGE)
                .build();

        PointHistory savedHistory = pointHistoryRepository.save(pointHistory);

        return PointChargeResponse.of(member, savedHistory);
    }
}
