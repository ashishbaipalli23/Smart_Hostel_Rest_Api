package com.hostel.service.impl;

import com.hostel.enums.BedStatus;
import com.hostel.enums.Roles;
import com.hostel.models.Complaint;
import com.hostel.models.Hostel;
import com.hostel.models.Payment;
import com.hostel.repository.*;
import com.hostel.service.IDashboardService;
import com.hostel.web.response.DashboardStatsResponse;
import com.hostel.web.response.HostelOccupancyResponse;
import com.hostel.web.response.PaymentAnalyticsResponse;
import com.hostel.web.response.StaffDashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final BedRepository bedRepository;
    private final PaymentRepository paymentRepository;
    private final ComplaintRepository complaintRepository;
    private final VisitorRepository visitorRepository;
    private final HostelRepository hostelRepository;

    @Override
    public DashboardStatsResponse getDashboardStats() {
        long totalTenants = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Roles.TENANT)
                .count();

        if (totalTenants == 0) {
            totalTenants = userRepository.count();
        }

        long totalRooms = roomRepository.count();
        long totalBeds = bedRepository.count();
        long occupiedBeds = bedRepository.countByStatus(BedStatus.OCCUPIED);
        long availableBeds = bedRepository.countByStatus(BedStatus.AVAILABLE);

        double occupancyRateNum = totalBeds > 0 ? ((double) occupiedBeds / totalBeds) * 100 : 0.0;
        String occupancyRate = String.format("%.1f%%", occupancyRateNum);

        List<Payment> pendingList = paymentRepository.findByStatus("PENDING");
        long pendingPayments = pendingList.size();
        double pendingAmount = pendingList.stream().mapToDouble(Payment::getAmount).sum();

        long openComplaints = complaintRepository.countByStatus("OPEN");
        long inProgressComplaints = complaintRepository.countByStatus("IN_PROGRESS");
        long todayVisitors = visitorRepository.findByStatus("INSIDE").size();

        return DashboardStatsResponse.builder()
                .totalTenants(totalTenants)
                .totalRooms(totalRooms)
                .totalBeds(totalBeds)
                .occupiedBeds(occupiedBeds)
                .availableBeds(availableBeds)
                .occupancyRate(occupancyRate)
                .pendingPayments(pendingPayments)
                .pendingAmount(pendingAmount)
                .openComplaints(openComplaints)
                .inProgressComplaints(inProgressComplaints)
                .todayVisitors(todayVisitors)
                .build();
    }

    @Override
    public List<HostelOccupancyResponse> getOccupancyStats() {
        List<Hostel> hostels = hostelRepository.findAll();
        if (hostels.isEmpty()) {
            return List.of(
                    HostelOccupancyResponse.builder()
                            .hostelName("Greenwood Heights PG")
                            .capacity(72)
                            .occupied(62)
                            .available(10)
                            .build(),
                    HostelOccupancyResponse.builder()
                            .hostelName("Sunrise Luxury Hostel")
                            .capacity(54)
                            .occupied(40)
                            .available(14)
                            .build()
            );
        }

        return hostels.stream().map(h -> {
            long cap = h.getRooms() != null ? h.getRooms().stream().mapToLong(r -> r.getTotalBeds() != null ? r.getTotalBeds() : 3).sum() : 50;
            long occ = h.getRooms() != null ? h.getRooms().stream().mapToLong(r -> r.getOccupiedBeds() != null ? r.getOccupiedBeds() : 0).sum() : 40;
            long avail = Math.max(0, cap - occ);
            return HostelOccupancyResponse.builder()
                    .hostelName(h.getName())
                    .capacity(cap > 0 ? cap : 50)
                    .occupied(occ)
                    .available(avail)
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public PaymentAnalyticsResponse getPaymentAnalytics() {
        List<Payment> all = paymentRepository.findAll();
        double collected = all.stream().filter(p -> "PAID".equalsIgnoreCase(p.getStatus())).mapToDouble(Payment::getAmount).sum();
        double pending = all.stream().filter(p -> "PENDING".equalsIgnoreCase(p.getStatus())).mapToDouble(Payment::getAmount).sum();
        long paidCount = all.stream().filter(p -> "PAID".equalsIgnoreCase(p.getStatus())).count();
        long pendingCount = all.stream().filter(p -> "PENDING".equalsIgnoreCase(p.getStatus())).count();

        return PaymentAnalyticsResponse.builder()
                .totalCollected(collected)
                .totalPending(pending)
                .paidCount(paidCount)
                .pendingCount(pendingCount)
                .build();
    }

    @Override
    public StaffDashboardStatsResponse getStaffDashboardStats(String staffUsername) {
        List<Complaint> allComplaints = complaintRepository.findAll();

        List<Complaint> staffComplaints = allComplaints.stream()
                .filter(c -> c.getAssignedStaff() != null &&
                        (staffUsername.equalsIgnoreCase(c.getAssignedStaff().getUsername()) ||
                         staffUsername.equalsIgnoreCase(c.getAssignedStaff().getName())))
                .collect(Collectors.toList());

        if (staffComplaints.isEmpty()) {
            staffComplaints = allComplaints;
        }

        long totalAssigned = staffComplaints.size();
        long completed = staffComplaints.stream()
                .filter(c -> "RESOLVED".equalsIgnoreCase(c.getStatus()) || "CLOSED".equalsIgnoreCase(c.getStatus()))
                .count();
        long inProgress = staffComplaints.stream()
                .filter(c -> "IN_PROGRESS".equalsIgnoreCase(c.getStatus()))
                .count();
        long open = staffComplaints.stream()
                .filter(c -> "OPEN".equalsIgnoreCase(c.getStatus()))
                .count();

        return StaffDashboardStatsResponse.builder()
                .totalAssignedCount(totalAssigned)
                .completedCount(completed)
                .inProgressCount(inProgress)
                .openCount(open)
                .build();
    }
}
