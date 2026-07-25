package com.hostel.events;


import java.time.LocalDate;


public record UserCreatedEvent(String name, String username, String phoneNumber, String aadhaarNumber, String address,
                               String city, String state, String pinCode, String role, LocalDate joiningDate,
                               String rawPassword) {


}
