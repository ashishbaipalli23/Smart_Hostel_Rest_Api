package com.hostel.service;

import com.hostel.exceptions.ResourceAlreadyExistsException;
import com.hostel.mapper.UserMapper;
import com.hostel.models.UserEntity;
import com.hostel.repository.UserRepository;
import com.hostel.web.request.UserRegistrationRequest;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public String registerUser(UserRegistrationRequest request) {

        log.info("Registering user with email: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {

            throw new ResourceAlreadyExistsException("User already exists with email: " + request.getUsername());

        }

        request.setPassword(passwordEncoder.encode(request.getPassword()));// password encryption
        request.setAadhaarNumber(passwordEncoder.encode(request.getAadhaarNumber()));// Aadhaar encryption

        UserEntity userEntity = userMapper.toEntity(request);
        log.info("entity data : {}", userEntity);

        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        return "User register with ID + " + savedUser.getId();
    }
}
