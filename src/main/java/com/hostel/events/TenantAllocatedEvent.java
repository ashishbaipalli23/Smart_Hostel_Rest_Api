package com.hostel.events;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TenantAllocatedEvent(

        Long allocationId,

        String hostelCode,

        String tenantName,

        String tenantUsername,

        String roomNumber,

        String bedNumber,

        LocalDate checkInDate,

        BigDecimal monthlyRent,

        BigDecimal depositAmount,

        LocalDate upcomingRentDate,

        LocalDate dueDate

) {}