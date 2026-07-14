package com.example.cafe.point.controller;

import com.example.cafe.global.dto.ApiResponse;
import com.example.cafe.point.dto.PointChargeRequest;
import com.example.cafe.point.dto.PointChargeResponse;
import com.example.cafe.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final PointService pointService;

    @PostMapping("/charge")
    public ResponseEntity<ApiResponse<PointChargeResponse>> chargePoint(@RequestBody @Valid PointChargeRequest request) {
        PointChargeResponse response = pointService.chargePoint(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
