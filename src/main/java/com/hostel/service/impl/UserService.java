package com.hostel.service.impl;

import com.hostel.events.UserCreatedEvent;
import com.hostel.exceptions.ResourceAlreadyExistsException;
import com.hostel.mapper.UserMapper;
import com.hostel.enums.Roles;
import com.hostel.models.UserEntity;
import com.hostel.repository.UserRepository;
import com.hostel.service.IUserService;
import com.hostel.utils.SecurityUtils;
import com.hostel.web.request.UserRegistrationRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements IUserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public String registerUser(UserRegistrationRequest request) {

        log.info("Registering user with email / username: {}", request.getUsername());

        // Default fallbacks for optional fields
        if (request.getPhoneNumber() == null || request.getPhoneNumber().trim().isEmpty()) {
            request.setPhoneNumber("+919876543210");
        }
        if (request.getAddress() == null || request.getAddress().trim().isEmpty()) {
            request.setAddress("Hostel Resident Address");
        }
        if (request.getCity() == null || request.getCity().trim().isEmpty()) {
            request.setCity("Bangalore");
        }
        if (request.getState() == null || request.getState().trim().isEmpty()) {
            request.setState("Karnataka");
        }
        if (request.getPincode() == null || request.getPincode().trim().isEmpty()) {
            request.setPincode("560001");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            request.setPassword("Tenant@123");
        }

        // SERVICE-LEVEL ROLE CHECK
        Roles currentUserRole = SecurityUtils.getCurrentUserRole();
        Roles requestedRole = request.getRole();

        if (currentUserRole == Roles.ADMIN &&
                (requestedRole != Roles.STAFF && requestedRole != Roles.TENANT)) {
            throw new AccessDeniedException("ADMIN can only create STAFF or TENANT");
        }

        if (currentUserRole == Roles.SUPER_ADMIN &&
                (requestedRole != Roles.ADMIN && requestedRole != Roles.STAFF && requestedRole != Roles.TENANT)) {
            throw new AccessDeniedException("SUPER_ADMIN can create ADMIN, STAFF, or TENANT");
        }

        // Check duplicates for username
        Optional<UserEntity> existingUser = userRepository.findByUsername(request.getUsername());
        if (existingUser.isPresent()) {
            throw new ResourceAlreadyExistsException("User email / username already exists: " + request.getUsername());
        }

        String rawPassword = request.getPassword();
        request.setPassword(passwordEncoder.encode(rawPassword));

        UserEntity userEntity = userMapper.toEntity(request);
        if (userEntity.getJoiningDate() == null) {
            userEntity.setJoiningDate(LocalDate.now());
        }
        userEntity.setIsEnabled(true);

        UserEntity savedUser = userRepository.save(userEntity);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        applicationEventPublisher.publishEvent(
                new UserCreatedEvent(
                        savedUser.getName(),
                        savedUser.getUsername(),
                        savedUser.getPhoneNumber(),
                        savedUser.getAadhaarNumber(),
                        savedUser.getAddress(),
                        savedUser.getCity(),
                        savedUser.getState(),
                        savedUser.getPincode(),
                        savedUser.getRole().name(),
                        savedUser.getJoiningDate(),
                        rawPassword
                )
        );

        return "User registered successfully with ID: " + savedUser.getId();
    }
}
