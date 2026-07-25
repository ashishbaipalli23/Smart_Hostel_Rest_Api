package com.hostel.controller;

import com.hostel.models.Hostel;
import com.hostel.service.IHostelService;
import com.hostel.web.request.BulkRoomCreateRequest;
import com.hostel.web.request.CreateHostelRequest;
import com.hostel.web.response.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hostels")
@RequiredArgsConstructor
public class HostelController {

    private final IHostelService hostelService;

    @GetMapping
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<APIResponse<List<Hostel>>> getAllHostels() {
        List<Hostel> hostels = hostelService.getAllHostels();
        APIResponse<List<Hostel>> apiResponse = APIResponse.<List<Hostel>>builder()
                .msg("Hostels list fetched successfully")
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .data(hostels)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'ADMIN', 'STAFF')")
    public ResponseEntity<APIResponse<Hostel>> getHostelById(@PathVariable Long id) {
        Hostel hostel = hostelService.getHostelById(id);
        APIResponse<Hostel> apiResponse = APIResponse.<Hostel>builder()
                .msg("Hostel details fetched")
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.OK.value())
                .data(hostel)
                .build();

        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<APIResponse<String>> createHostel(@Valid @RequestBody CreateHostelRequest request) {
        String responseMsg = hostelService.createHostel(request);
        APIResponse<String> apiResponse = APIResponse.<String>builder()
                .msg("Hostel Created !")
                .timeStamp(LocalDateTime.now())
                .status(HttpStatus.CREATED.value())
                .data(responseMsg)
                .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @PostMapping("/bulk-create-rooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> createBulkRooms(@Valid @RequestBody BulkRoomCreateRequest request) {
        return ResponseEntity.ok(
                hostelService.bulkCreateRooms(request)
        );
    }
}