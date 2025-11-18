package com.cotato.networking.weather.global.config;

import com.cotato.networking.weather.global.exception.ErrorStatus;
import com.cotato.networking.weather.global.exception.GeneralException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    public static final String SESSION_USER_ID = "SESSION_USER_ID";

    @Override
public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_USER_ID) == null) {
            throw new GeneralException(ErrorStatus.UNAUTHORIZED);
        }
        return true;
    }
}
