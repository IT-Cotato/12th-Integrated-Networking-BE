package com.team3.backend.config;

import com.team3.backend.domain.application.UserService;
import com.team3.backend.domain.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class SessionLoginInterceptor implements HandlerInterceptor {

    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        Long userId = (Long) request.getSession().getAttribute("LOGIN_USER");

        if (userId == null) {
            return true;
        }

        User user = userService.getById(userId);

        request.setAttribute("loginUser", user);

        return true;
    }
}