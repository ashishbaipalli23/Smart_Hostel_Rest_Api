package com.hostel.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String body = """
        {
            "timeStamp": "%s",
            "status": 401,
            "errorMsg": "Unauthorized - Invalid or Expired Token",
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