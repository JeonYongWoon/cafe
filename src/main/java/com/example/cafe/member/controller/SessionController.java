package com.example.cafe.member.controller;

import com.example.cafe.global.dto.ApiResponse;
import com.example.cafe.member.dto.SessionCreateRequest;
import com.example.cafe.member.dto.SessionCreateResponse;
import com.example.cafe.member.service.SessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    @PostMapping
    public ResponseEntity<ApiResponse<SessionCreateResponse>> login(
            @RequestBody SessionCreateRequest request,
            HttpServletRequest httpRequest) {
        SessionCreateResponse response = sessionService.login(request);
        
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("LOGIN_MEMBER", response);
        
        return ResponseEntity
                .ok(ApiResponse.success(response));
    }
}
