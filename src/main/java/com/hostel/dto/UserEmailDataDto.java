package com.hostel.dto;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserEmailDataDto(
        String name,
        String username,
        String phoneNumber,
        String aadhaarNumber,
        String address,
        String city,
        String state,
        String pinCode,
        String role,
        LocalDate joiningDate
) {}