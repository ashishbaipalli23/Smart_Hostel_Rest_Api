package com.hostel.service.impl;
import com.hostel.utils.Roles;
import com.hostel.utils.JwtKeyGenerator;
import com.hostel.service.IUserService;
import com.hostel.service.IEmailService;
import com.hostel.utils.FileEncryptionUtil;

import com.hostel.web.request.LoginRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.hostel.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public String login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken((UserDetails) authentication.getPrincipal());
        }

        throw new RuntimeException("Invalid credentials");
    }
}
