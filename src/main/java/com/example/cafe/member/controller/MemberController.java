package com.example.cafe.member.controller;

import com.example.cafe.global.dto.ApiResponse;
import com.example.cafe.member.dto.MemberSignupRequest;
import com.example.cafe.member.dto.MemberSignupResponse;
import com.example.cafe.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<ApiResponse<MemberSignupResponse>> signup(@RequestBody MemberSignupRequest request) {
        MemberSignupResponse response = memberService.signup(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response));
    }
}
