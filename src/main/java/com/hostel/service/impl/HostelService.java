package com.hostel.service.impl;

import com.hostel.enums.BedStatus;
import com.hostel.enums.Roles;
import com.hostel.enums.RoomStatus;
import com.hostel.enums.SharingType;
import com.hostel.events.HostelCreatedEvent;
import com.hostel.exceptions.ResourceAlreadyExistsException;
import com.hostel.exceptions.ResourceNotFoundException;
import com.hostel.mapper.HostelMapper;
import com.hostel.models.*;
import com.hostel.repository.*;
import com.hostel.security.SecurityService;
import com.hostel.service.IHostelService;
import com.hostel.web.request.BulkRoomCreateRequest;
import com.hostel.web.request.CreateHostelRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class HostelService implements IHostelService {

    private final HostelRepository hostelRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final UserRepository userRepository;
    private final AdminHostelMappingRepository adminHostelMappingRepository;
    private final HostelMapper hostelMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final SecurityService securityService;

    @Override
    public List<Hostel> getAllHostels() {
        return hostelRepository.findAll();
    }

    @Override
    public Hostel getHostelById(Long id) {
        return hostelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hostel not found with ID: " + id));
    }

    @Override
    @Transactional
    public String createHostel(CreateHostelRequest request) {

        if (hostelRepository.existsByCode(request.getCode())) {
            throw new ResourceAlreadyExistsException("Hostel code already exists");
        }

        Hostel hostelEntity = hostelMapper.toEntity(request);

        Hostel saved = hostelRepository.save(hostelEntity);

        eventPublisher.publishEvent(new HostelCreatedEvent(request.getCode(), request.getName(), request.getAddress(), request.getCity(), request.getState(), request.getPinCode(), request.getGenderType(), request.getTotalFloors(), request.getActive()));

        return "Hostel created successfully with CODE : " + saved.getCode();
    }

    @Override
    @Transactional
    public String bulkCreateRooms(BulkRoomCreateRequest request) {

        Hostel hostel = hostelRepository.findByCode(request.getHostelCode())
                .orElseThrow(() -> new ResourceNotFoundException("Hostel Code not found"));

        securityService.validateHostelAccess(hostel.getCode(), hostel.getId());

        List<Room> roomsToSave = new ArrayList<>();

        int totalBeds = getTotalBeds(request.getSharingType());

        for (int roomNo = request.getStartRoomNumber(); roomNo <= request.getEndRoomNumber(); roomNo++) {

            String finalRoomNumber = request.getRoomPrefix() + roomNo;

            boolean exists = roomRepository.existsByHostelIdAndRoomNumber(hostel.getId(), finalRoomNumber);

            if (exists) {
                log.warn("Room already exists : {}", finalRoomNumber);
                continue;
            }

            Room room = Room.builder()
                    .hostel(hostel)
                    .roomNumber(finalRoomNumber)
                    .floorNumber(request.getFloorNumber())
                    .sharingType(request.getSharingType())
                    .totalBeds(totalBeds).occupiedBeds(0)
                    .rentPerBed(request.getRentPerBed())
                    .status(RoomStatus.AVAILABLE)
                    .active(true)
                    .build();

            roomsToSave.add(room);
        }

        List<Room> savedRooms = roomRepository.saveAll(roomsToSave);

        List<Bed> bedsToSave = new ArrayList<>();

        for (Room savedRoom : savedRooms) {

            for (int i = 1; i <= savedRoom.getTotalBeds(); i++) {

                String bedNumber = savedRoom.getRoomNumber() + "-B" + i;

                Bed bed = Bed.builder().room(savedRoom).bedNumber(bedNumber).status(BedStatus.AVAILABLE).active(true).build();

                bedsToSave.add(bed);
            }
        }

        bedRepository.saveAll(bedsToSave);

        return "Rooms and beds created successfully";
    }

    @Override
    @Transactional
    public String assignAdminToHostel(String hostelCode, String username) {

        Hostel hostel = hostelRepository.findByCode(hostelCode)
                .orElseThrow(() -> new ResourceNotFoundException("Hostel not found"));

        UserEntity admin = userRepository.findByUsernameAndRole(username, Roles.ADMIN)
                .orElseThrow(() -> new ResourceNotFoundException("Admin not found"));

        boolean alreadyAssigned = adminHostelMappingRepository.existsByAdminIdAndHostelId(admin.getId(), hostel.getId());

        if (alreadyAssigned) {
            throw new ResourceAlreadyExistsException("Admin already assigned");
        }

        AdminHostelMapping mapping = AdminHostelMapping.builder()
                .admin(admin)
                .hostel(hostel)
                .assignedAt(LocalDateTime.now())
                .build();

        adminHostelMappingRepository.save(mapping);

        return "Admin assigned successfully";
    }

    private int getTotalBeds(SharingType sharingType) {

        return switch (sharingType) {

            case SINGLE -> 1;
            case TWO_SHARE -> 2;
            case THREE_SHARE -> 3;
            case FOUR_SHARE -> 4;
            case FIVE_SHARE -> 5;
        };
    }
}