package com.hostel.web.request;

import com.hostel.enums.SharingType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CreateRoomRequest {
    private String roomNumber;
    private Integer floorNumber;
    private SharingType sharingType = SharingType.TWO_SHARE;
    private Integer totalBeds = 2;
    private BigDecimal rentPerBed = new BigDecimal("6500");
    private Long hostelId;
    private String hostelCode;
}
