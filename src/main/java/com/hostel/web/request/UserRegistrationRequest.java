package com.hostel.web.request;

import com.hostel.utils.Roles;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserRegistrationRequest {


    /**
     * username : john.doe@gmail.com
     * password : StrongPassword@123
     * phoneNumber : +918797812345
     * aadhaarNumber : 123456789012
     * address : 123 Main Street
     * city : Hyderabad
     * state : Telangana
     * pincode : 500001
     * role : TENANT
     */


    @Email
    @NotBlank
    private String username;

    @NotBlank
    private String name;

    @NotBlank
    private String password;

    @Pattern(regexp = "^\\+91\\d{10}$",message = "must be 10 digits exclude +91")
    private String phoneNumber;

    @Pattern(regexp = "\\d{12}")
    private String aadhaarNumber;

    @NotBlank
    private String address;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @Pattern(regexp = "\\d{6}")
    private String pincode;

    private Roles role;

    private LocalDate joiningDate;

}
