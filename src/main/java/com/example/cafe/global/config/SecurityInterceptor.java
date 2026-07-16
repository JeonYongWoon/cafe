package com.example.cafe.global.config;

import com.example.cafe.global.error.CustomException;
import com.example.cafe.global.error.ErrorCode;
import com.example.cafe.member.domain.MemberRole;
import com.example.cafe.member.dto.SessionCreateResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new CustomException(ErrorCode.MEMBER_UNAUTHORIZED);
        }

        SessionCreateResponse loginMember = (SessionCreateResponse) session.getAttribute("LOGIN_MEMBER");
        if (loginMember == null) {
            throw new CustomException(ErrorCode.MEMBER_UNAUTHORIZED);
        }

        if (request.getRequestURI().startsWith("/admin")) {
            if (loginMember.getRole() != MemberRole.ADMIN) {
                throw new CustomException(ErrorCode.ORDER_UNAUTHORIZED_ACCESS);
            }
        }

        return true;
    }
}
