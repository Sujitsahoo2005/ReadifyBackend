package com.readify.example.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

public class ApiAccessInterceptor implements HandlerInterceptor {
    private static final String BLOCKED_PATH_PREFIX = "/vendor/";
    private static final List<String> ALLOWED_ORIGINS = Arrays.asList("https://staffezy.com", "https://app.staffezy.com","https://staffezy-app.vercel.app", "https://staffezy-officials.netlify.app");
    private static final List<String> ALLOWED_IPS = Arrays.asList("");
    private static final List<String> HEALTH_CHECK_PATH = Arrays.asList("/api/v1/test-health","/api/v4/company-info-save","/api/v4/company-password-change","/api/v4/reload-the-subscription");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        String origin = request.getHeader("Origin");
//        String ip = request.getRemoteAddr();
//        String requestPath = request.getRequestURI();
//    // Block access to all paths under /vendor/
//        if (requestPath.startsWith(BLOCKED_PATH_PREFIX)) {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return false;
//        }
//        // Allow the health check endpoint for everyone
//        if (HEALTH_CHECK_PATH.contains(requestPath)) {
//            return true;
//        }
//
//        // Restrict other endpoints based on origin and IP
//        if (ALLOWED_ORIGINS.contains(origin) || ALLOWED_IPS.contains(ip)) {
//            return true;
//        } else {
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//            return false;
//        }
        return true;
    }
}
