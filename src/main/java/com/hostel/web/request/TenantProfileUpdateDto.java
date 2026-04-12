package com.hostel.web.request;

import lombok.Data;

@Data
public class TenantProfileUpdateDto {
    private String name;
    private String phoneNumber;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String aadhaarNumber;
}
