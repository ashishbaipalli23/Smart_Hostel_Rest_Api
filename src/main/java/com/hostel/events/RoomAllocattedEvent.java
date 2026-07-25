package com.hostel.events;

import java.math.BigDecimal;
import java.time.LocalDate;

    public record RoomAllocattedEvent(

        String roomNumber,
        String bedNumber,
        LocalDate joinedDate,
        BigDecimal rentForBed, //fee per month
        LocalDate upCommingRentDate,
        LocalDate dueDate // next month same date on the joined date
) {}


