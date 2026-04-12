package com.hostel.service;

import com.hostel.web.request.UserRegistrationRequest;

public interface IUserService {


    /**
     * This method is used to register new user
     * @param request contains user details
     * @return string as msg
     */
    String registerUser(UserRegistrationRequest request);

}
