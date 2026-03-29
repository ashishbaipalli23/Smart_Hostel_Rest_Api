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

    private final IEmailService emailService;

    @Override
    @Transactional
    public String registerUser(UserRegistrationRequest request) {

        log.info("Registering user with email: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {

            throw new ResourceAlreadyExistsException("User already exists with email: " + request.getUsername());

        }

        String rawPassword = request.getPassword();
        request.setPassword(passwordEncoder.encode(rawPassword));// password encryption
       // request.setAadhaarNumber(passwordEncoder.encode(request.getAadhaarNumber()));// Aadhaar encryption

        UserEntity userEntity = userMapper.toEntity(request);
        userEntity.setJoiningDate(java.time.LocalDate.now());
        log.info("entity data : {}", userEntity);

        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        emailService.sendUserRegistrationEmail(savedUser, rawPassword);
        emailService.sendAdminNotificationEmail(savedUser);

        return "User register with ID + " + savedUser.getId();
    }
}
