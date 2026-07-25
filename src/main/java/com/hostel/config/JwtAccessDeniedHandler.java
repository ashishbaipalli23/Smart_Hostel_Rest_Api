package com.hostel.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException ex) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        String body = """
        {
            "timeStamp": "%s",
            "status": 403,
            "errorMsg": "Forbidden - You don’t have permission",
            "path": "%s",
            "method": "%s"
        }
        """.formatted(
                java.time.LocalDateTime.now(),
                request.getRequestURI(),
                request.getMethod()
        );

        response.getWriter().write(body);
    }
}