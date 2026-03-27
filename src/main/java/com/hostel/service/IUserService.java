package com.hostel.service;

import com.hostel.web.request.UserRegistrationRequest;

public interface IUserService {


    //register the user
    String registerUser(UserRegistrationRequest request);

}
