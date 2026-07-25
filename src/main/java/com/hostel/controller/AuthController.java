package com.hostel.controller;


import com.hostel.service.impl.AuthService;
import com.hostel.service.impl.UserService;
import com.hostel.web.request.LoginRequest;
import com.hostel.web.request.UserRegistrationRequest;
import com.hostel.web.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
@Tag(name = "Authentication", description = "APIs for user authentication and registration")
public class AuthController {

    private final UserService userService;
    private final AuthService authService;

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account with username and password"
    )


    @PostMapping("/login")
    public ResponseEntity<APIResponse<String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        String jwtToken = authService.login(loginRequest);
        APIResponse<String> apiResponse = APIResponse.<String>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Login Successful")
                .data(jwtToken)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }


}
