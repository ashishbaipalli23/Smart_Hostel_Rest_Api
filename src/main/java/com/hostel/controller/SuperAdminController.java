package com.hostel.controller;

import com.hostel.enums.Roles;
import com.hostel.models.Hostel;
import com.hostel.models.UserEntity;
import com.hostel.repository.UserRepository;
import com.hostel.service.IHostelService;
import com.hostel.service.IUserService;
import com.hostel.web.request.CreateHostelRequest;
import com.hostel.web.request.UserRegistrationRequest;
import com.hostel.web.response.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/super-admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
@RequiredArgsConstructor
public class SuperAdminController {

    private final IUserService userService;
    private final IHostelService hostelService;
    private final UserRepository userRepository;

    @GetMapping("/admins")
    public ResponseEntity<APIResponse<List<UserEntity>>> getAllAdmins() {
        List<UserEntity> admins = userRepository.findByRole(Roles.ADMIN);
        APIResponse<List<UserEntity>> apiResponse = APIResponse.<List<UserEntity>>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Admins list fetched")
                .data(admins)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/admin/register")
    public ResponseEntity<APIResponse<String>> createAdmin(@Valid @RequestBody UserRegistrationRequest request) {
        String responseMsg = userService.registerUser(request);
        APIResponse<String> apiResponse = APIResponse.<String>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Admin Created Successfully!")
                .data(responseMsg)
                .status(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/hostels")
    public ResponseEntity<APIResponse<List<Hostel>>> getAllHostels() {
        List<Hostel> hostels = hostelService.getAllHostels();
        APIResponse<List<Hostel>> apiResponse = APIResponse.<List<Hostel>>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Global Hostels list")
                .data(hostels)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/hostels")
    public ResponseEntity<APIResponse<String>> createHostel(@Valid @RequestBody CreateHostelRequest request) {
        String responseMsg = hostelService.createHostel(request);
        APIResponse<String> apiResponse = APIResponse.<String>builder()
                .msg("Hostel Created !")
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .data(responseMsg)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @PostMapping("/hostels/{hostelCode}/assign-admin/{username}")
    public ResponseEntity<APIResponse<String>> assignAdminToHostel(
            @PathVariable String hostelCode,
            @PathVariable String username) {

        String msg = hostelService.assignAdminToHostel(hostelCode, username);
        APIResponse<String> apiResponse = APIResponse.<String>builder()
                .timeStamp(LocalDateTime.now())
                .msg(msg)
                .data(msg)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
