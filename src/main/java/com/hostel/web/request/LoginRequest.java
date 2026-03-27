package com.hostel.web.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "username not be empty")
        String username,

        @NotBlank(message = "Password not be empty")
        String password
) { }
