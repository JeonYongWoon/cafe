package com.example.cafe.member.controller;

import com.example.cafe.global.dto.ApiResponse;
import com.example.cafe.member.dto.MemberResponse;
import com.example.cafe.member.dto.MemberSignupRequest;
import com.example.cafe.member.dto.MemberSignupResponse;
import com.example.cafe.member.service.MemberService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/{memberId}")
    public ResponseEntity<ApiResponse<MemberResponse>> getMemberDetail(@PathVariable @Positive Long memberId) {
        MemberResponse response = memberService.getMemberDetail(memberId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<MemberSignupResponse>> signup(@RequestBody MemberSignupRequest request) {
        MemberSignupResponse response = memberService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
