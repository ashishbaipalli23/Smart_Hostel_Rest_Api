package com.hostel.web.request;

import com.hostel.enums.SharingType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BulkRoomCreateRequest {

    @NotNull(message = "hostelCode required")
    private String hostelCode;

    @NotNull(message = "field required")
    private Integer floorNumber;

    @NotNull(message = "field required")
    private Integer startRoomNumber;

    @NotNull(message = "field required")
    private Integer endRoomNumber;

    @NotNull(message = "field required")
    private SharingType sharingType;

    @NotNull(message = "field required")
    private BigDecimal rentPerBed;

    @NotNull(message = "field required")
    private String roomPrefix;
}