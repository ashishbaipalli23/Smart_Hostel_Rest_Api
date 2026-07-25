package com.hostel.events;

import com.hostel.enums.GenderType;
import lombok.Builder;

@Builder
public record HostelCreatedEvent(
        String code,
        String name,
        String address,
        String city,
        String state,
        String pinCode,
        GenderType genderType,
        Integer totalFloors,
        Boolean active
){}
