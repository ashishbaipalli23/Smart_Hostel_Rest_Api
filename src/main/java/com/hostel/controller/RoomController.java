package com.hostel.controller;

import com.hostel.enums.BedStatus;
import com.hostel.enums.RoomStatus;
import com.hostel.enums.SharingType;
import com.hostel.models.Bed;
import com.hostel.models.Hostel;
import com.hostel.models.Room;
import com.hostel.repository.BedRepository;
import com.hostel.repository.HostelRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.web.request.CreateRoomRequest;
import com.hostel.web.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final HostelRepository hostelRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN', 'ROLE_STAFF', 'ROLE_TENANT')")
    public ResponseEntity<APIResponse<List<Room>>> getRooms(@RequestParam(required = false) Long hostelId) {
        List<Room> rooms;
        if (hostelId != null) {
            rooms = roomRepository.findByHostelId(hostelId);
            if (rooms == null || rooms.isEmpty()) {
                rooms = roomRepository.findAll();
            }
        } else {
            rooms = roomRepository.findAll();
        }

        APIResponse<List<Room>> apiResponse = APIResponse.<List<Room>>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Rooms fetched successfully")
                .data(rooms)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    public ResponseEntity<APIResponse<Room>> createRoom(@RequestBody(required = false) CreateRoomRequest request) {
        if (request == null) {
            request = new CreateRoomRequest();
        }

        Hostel hostel = null;
        if (request.getHostelId() != null) {
            hostel = hostelRepository.findById(request.getHostelId()).orElse(null);
        }
        if (hostel == null && request.getHostelCode() != null) {
            hostel = hostelRepository.findByCode(request.getHostelCode()).orElse(null);
        }
        if (hostel == null) {
            List<Hostel> hostels = hostelRepository.findAll();
            if (!hostels.isEmpty()) {
                hostel = hostels.get(0);
            }
        }

        String roomNum = request.getRoomNumber() != null ? request.getRoomNumber() : "R-101";

        Room room = Room.builder()
                .roomNumber(roomNum)
                .floorNumber(request.getFloorNumber() != null ? request.getFloorNumber() : 1)
                .sharingType(request.getSharingType() != null ? request.getSharingType() : SharingType.TWO_SHARE)
                .totalBeds(request.getTotalBeds() != null ? request.getTotalBeds() : 2)
                .occupiedBeds(0)
                .rentPerBed(request.getRentPerBed() != null ? request.getRentPerBed() : new BigDecimal("6500"))
                .status(RoomStatus.AVAILABLE)
                .active(true)
                .hostel(hostel)
                .build();

        Room savedRoom = roomRepository.save(room);

        int count = savedRoom.getTotalBeds() != null ? savedRoom.getTotalBeds() : 2;
        List<Bed> beds = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            Bed bed = Bed.builder()
                    .room(savedRoom)
                    .bedNumber(savedRoom.getRoomNumber() + "-B" + i)
                    .status(BedStatus.AVAILABLE)
                    .active(true)
                    .build();
            beds.add(bed);
        }
        bedRepository.saveAll(beds);

        APIResponse<Room> apiResponse = APIResponse.<Room>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Room created successfully")
                .data(savedRoom)
                .status(HttpStatus.CREATED.value())
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }
}
