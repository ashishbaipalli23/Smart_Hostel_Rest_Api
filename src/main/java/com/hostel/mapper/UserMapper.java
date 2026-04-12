package com.hostel.mapper;

import com.hostel.models.UserEntity;
import com.hostel.web.request.UserRegistrationRequest;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserRegistrationRequest requestDto);

    UserRegistrationRequest toDto(UserEntity entity);
}
