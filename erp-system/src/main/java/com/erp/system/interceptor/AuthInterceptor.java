package com.erp.system.interceptor;

import cn.hutool.core.util.StrUtil;
import com.erp.common.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equals(request.getMethod())) return true;

        String token = request.getHeader("Authorization");
        if (StrUtil.isBlank(token) || !token.startsWith("Bearer ")) {
            response.setStatus(401);
            return false;
        }

        try {
            token = token.substring(7);
            Long userId = jwtUtil.getUserId(token);
            request.setAttribute("userId", userId);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            return false;
        }
    }
}
