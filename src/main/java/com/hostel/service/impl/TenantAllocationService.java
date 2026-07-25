package com.hostel.service.impl;

import com.hostel.enums.AllocationStatus;
import com.hostel.enums.BedStatus;
import com.hostel.enums.Roles;
import com.hostel.enums.RoomStatus;
import com.hostel.events.TenantAllocatedEvent;
import com.hostel.exceptions.ResourceNotFoundException;
import com.hostel.models.*;
import com.hostel.repository.*;
import com.hostel.security.SecurityService;
import com.hostel.service.ITenantAllocationService;
import com.hostel.web.request.AllocateTenantRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class TenantAllocationService implements ITenantAllocationService {

    private final UserRepository userRepository;
    private final BedRepository bedRepository;
    private final RoomRepository roomRepository;
    private final HostelRepository hostelRepository;
    private final TenantAllocationRepository allocationRepository;
    private final SecurityService securityService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    @Transactional
    public String allocateTenant(AllocateTenantRequest request) {
        log.info("Allocating tenant payload: {}", request);

        // STEP 1 → RESOLVE BED
        Bed bed = null;
        if (request.getBedId() != null) {
            bed = bedRepository.findById(request.getBedId()).orElse(null);
        }
        if (bed == null && request.getHostelCode() != null && request.getBedNumber() != null) {
            bed = bedRepository.findByRoom_Hostel_CodeAndBedNumber(request.getHostelCode(), request.getBedNumber())
                    .orElse(null);
        }
        if (bed == null) {
            throw new ResourceNotFoundException("Bed not found for allocation request");
        }

        // STEP 2 → RESOLVE HOSTEL CODE
        String hostelCode = request.getHostelCode();
        if (hostelCode == null || hostelCode.trim().isEmpty()) {
            if (bed.getRoom() != null && bed.getRoom().getHostel() != null) {
                hostelCode = bed.getRoom().getHostel().getCode();
            } else {
                hostelCode = "GWH01";
            }
        }

        // STEP 3 → RESOLVE TENANT
        UserEntity tenant = null;
        if (request.getTenantId() != null) {
            tenant = userRepository.findById(request.getTenantId()).orElse(null);
        }
        if (tenant == null && request.getTenantUsername() != null) {
            tenant = userRepository.findByUsername(request.getTenantUsername()).orElse(null);
        }
        if (tenant == null && request.getTenantUsername() != null) {
            tenant = userRepository.findByUsernameAndRole(request.getTenantUsername(), Roles.TENANT).orElse(null);
        }
        if (tenant == null) {
            throw new ResourceNotFoundException("Tenant not found for allocation request");
        }

        // STEP 4 → STRICT VALIDATION: PREVENT DUAL ROOM ALLOCATION FOR SAME TENANT
        boolean alreadyAllocated = allocationRepository.existsByTenantIdAndAllocationStatus(tenant.getId(), AllocationStatus.ACTIVE);
        if (alreadyAllocated) {
            log.warn("Allocation rejected: Tenant ID {} ({}) is already assigned to an active room bed.", tenant.getId(), tenant.getName());
            throw new IllegalStateException("Tenant " + tenant.getName() + " (" + tenant.getUsername() + ") is already allocated to an active room bed. Double allocation is not allowed.");
        }

        // STEP 5 → RESOLVE RENT AND DEPOSIT
        BigDecimal monthlyRent = request.getMonthlyRent();
        if (monthlyRent == null || monthlyRent.compareTo(BigDecimal.ZERO) <= 0) {
            monthlyRent = new BigDecimal("6500.00");
        }

        BigDecimal depositAmount = request.getDepositAmount();
        if (depositAmount == null || depositAmount.compareTo(BigDecimal.ZERO) <= 0) {
            depositAmount = new BigDecimal("10000.00");
        }

        LocalDate checkInDate = request.getCheckInDate();
        if (checkInDate == null) {
            checkInDate = LocalDate.now();
        }

        // STEP 6 → CHECK BED AVAILABILITY
        if (bed.getStatus() == BedStatus.OCCUPIED) {
            throw new IllegalStateException("Bed is already occupied by another resident.");
        }

        // STEP 7 → CREATE ALLOCATION
        TenantAllocation allocation = TenantAllocation.builder()
                .tenant(tenant)
                .bed(bed)
                .checkInDate(checkInDate)
                .monthlyRent(monthlyRent)
                .depositAmount(depositAmount)
                .allocationStatus(AllocationStatus.ACTIVE)
                .active(true)
                .build();

        allocationRepository.save(allocation);

        // STEP 8 → UPDATE BED STATUS
        bed.setStatus(BedStatus.OCCUPIED);
        bedRepository.save(bed);

        // STEP 9 → UPDATE ROOM OCCUPANCY
        Room room = bed.getRoom();
        if (room != null) {
            int currentOccupied = room.getOccupiedBeds() != null ? room.getOccupiedBeds() : 0;
            room.setOccupiedBeds(currentOccupied + 1);

            int totalBeds = room.getTotalBeds() != null ? room.getTotalBeds() : 1;
            if (room.getOccupiedBeds() >= totalBeds) {
                room.setStatus(RoomStatus.FULL);
            }
            roomRepository.save(room);
        }

        log.info("Tenant {} allocated to bed ID {}", tenant.getName(), bed.getId());

        LocalDate dueDate = checkInDate.plusMonths(1);
        LocalDate upcomingRentDate = dueDate.minusDays(5);

        try {
            applicationEventPublisher.publishEvent(
                    new TenantAllocatedEvent(
                            allocation.getId(),
                            hostelCode,
                            tenant.getName(),
                            tenant.getUsername(),
                            room != null ? room.getRoomNumber() : "A-101",
                            bed.getBedNumber(),
                            allocation.getCheckInDate(),
                            allocation.getMonthlyRent(),
                            allocation.getDepositAmount(),
                            upcomingRentDate,
                            dueDate
                    )
            );
        } catch (Exception e) {
            log.warn("Event publishing skipped: {}", e.getMessage());
        }

        return "Tenant allocated successfully";
    }
}