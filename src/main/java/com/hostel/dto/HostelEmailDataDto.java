package com.hostel.dto;

import com.hostel.enums.GenderType;
import lombok.Builder;

@Builder
public record HostelEmailDataDto (
        String code,
        String name,
        String address,
        String city,
        String state,
        String pinCode,
        GenderType genderType,
        Integer totalFloors
){ }
