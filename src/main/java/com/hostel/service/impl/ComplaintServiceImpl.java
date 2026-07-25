package com.hostel.service.impl;

import com.hostel.exceptions.ResourceNotFoundException;
import com.hostel.models.Complaint;
import com.hostel.models.ComplaintUpdate;
import com.hostel.models.UserEntity;
import com.hostel.repository.ComplaintRepository;
import com.hostel.repository.UserRepository;
import com.hostel.service.IComplaintService;
import com.hostel.web.request.CreateComplaintRequest;
import com.hostel.web.request.UpdateComplaintStatusRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintServiceImpl implements IComplaintService {


    private final ComplaintRepository complaintRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Complaint createComplaint(CreateComplaintRequest request) {
        UserEntity tenant = userRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        Complaint complaint = Complaint.builder()
                .title(request.getTitle())
                .category(request.getCategory())
                .description(request.getDescription())
                .roomNumber(request.getRoomNumber() != null ? request.getRoomNumber() : "A-101")
                .status("OPEN")
                .tenant(tenant)
                .build();

        ComplaintUpdate initialUpdate = ComplaintUpdate.builder()
                .complaint(complaint)
                .updatedBy(tenant.getUsername())
                .comment("Complaint logged: " + request.getTitle())
                .status("OPEN")
                .build();

        complaint.getUpdates().add(initialUpdate);
        return complaintRepository.save(complaint);
    }

    @Override
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }

    @Override
    public Complaint getComplaintById(Long id) {
        return complaintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint not found with ID: " + id));
    }

    @Override
    @Transactional
    public Complaint assignStaff(Long complaintId, Long staffId) {
        Complaint complaint = getComplaintById(complaintId);
        UserEntity staff = userRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff not found"));

        complaint.setAssignedStaff(staff);
        complaint.setStatus("IN_PROGRESS");

        ComplaintUpdate update = ComplaintUpdate.builder()
                .complaint(complaint)
                .updatedBy("Admin")
                .comment("Assigned to staff: " + staff.getUsername())
                .status("IN_PROGRESS")
                .build();

        complaint.getUpdates().add(update);
        return complaintRepository.save(complaint);
    }

    @Override
    @Transactional
    public Complaint updateStatus(Long complaintId, UpdateComplaintStatusRequest request, String updatedBy) {
        Complaint complaint = getComplaintById(complaintId);
        complaint.setStatus(request.getStatus());

        ComplaintUpdate update = ComplaintUpdate.builder()
                .complaint(complaint)
                .updatedBy(updatedBy)
                .comment(request.getComment() != null ? request.getComment() : "Status updated to " + request.getStatus())
                .status(request.getStatus())
                .build();

        complaint.getUpdates().add(update);
        return complaintRepository.save(complaint);
    }
}
