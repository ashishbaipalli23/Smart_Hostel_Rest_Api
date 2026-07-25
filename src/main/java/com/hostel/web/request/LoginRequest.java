package com.hostel.web.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "username not be empty")
        String username,

        @NotBlank(message = "Password not be empty")
        @Size(max = 9,min = 6,message = "Max 9 Min 6 length")
        String password
) { }
