package com.hostel.controller;


import com.hostel.service.impl.AuthService;
import com.hostel.service.impl.UserService;
import com.hostel.web.request.LoginRequest;
import com.hostel.web.request.UserRegistrationRequest;
import com.hostel.web.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> userRegistration(@Valid @RequestBody UserRegistrationRequest request) {
        String responseMsg = userService.registerUser(request);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .timeStamp(LocalDateTime.now())
                .msg("User Saved !")
                .data(responseMsg)
                .status(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        String jwtToken = authService.login(loginRequest);
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Login Successful")
                .data(jwtToken)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }


}
