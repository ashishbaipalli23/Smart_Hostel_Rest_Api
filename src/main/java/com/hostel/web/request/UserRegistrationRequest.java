package com.hostel.web.request;

import com.hostel.enums.Roles;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegistrationRequest {

    @NotBlank(message = "Email / Username is required")
    @Schema(description = "User Email / Username", example = "tenant@gmail.com")
    private String username;

    @NotBlank(message = "Full Name is required")
    private String name;

    @NotBlank(message = "Password is required")
    @Size(min = 4, max = 100, message = "Password must be at least 4 characters")
    private String password;

    private String phoneNumber;

    private String aadhaarNumber;

    private String address;

    private String city;

    private String state;

    private String pincode;

    @NotNull(message = "Role is required")
    private Roles role;

    private LocalDate joiningDate;
}
