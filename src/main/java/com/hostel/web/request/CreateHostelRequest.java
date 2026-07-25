package com.hostel.web.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hostel.enums.GenderType;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateHostelRequest {

    @NotBlank(message = "filed should not be empty")
    private String name;

    @NotBlank(message = "filed should not be empty")
    private String code;

    @NotBlank(message = "filed should not be empty")
    private String address;

    @NotBlank(message = "filed should not be empty")
    private String city;

    @NotBlank(message = "filed should not be empty")
    private String state;

    @NotBlank(message = "filed should not be empty")
    @JsonProperty("pincode")
    private String pinCode;

    @NotNull(message = "filed should not be empty")
    private GenderType genderType;

    @NotNull(message = "Field should not be empty")
    @Min(value = 1, message = "Minimum floors should be 1")
    @Max(value = 10, message = "Maximum floors should be 10")
    private Integer totalFloors;

    @AssertTrue
    @NotNull(message = "filed should not be empty")
    private Boolean active;

}