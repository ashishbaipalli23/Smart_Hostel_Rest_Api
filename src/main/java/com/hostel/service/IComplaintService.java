package com.hostel.service;

import com.hostel.models.Complaint;
import com.hostel.web.request.CreateComplaintRequest;
import com.hostel.web.request.UpdateComplaintStatusRequest;
import java.util.List;

public interface IComplaintService {
    Complaint createComplaint(CreateComplaintRequest request);
    List<Complaint> getAllComplaints();
    Complaint getComplaintById(Long id);
    Complaint assignStaff(Long complaintId, Long staffId);
    Complaint updateStatus(Long complaintId, UpdateComplaintStatusRequest request, String updatedBy);
}
