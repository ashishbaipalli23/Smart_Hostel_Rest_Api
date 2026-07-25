package com.hostel.service;

import com.hostel.models.Hostel;
import com.hostel.web.request.BulkRoomCreateRequest;
import com.hostel.web.request.CreateHostelRequest;
import java.util.List;

public interface IHostelService {

    String createHostel(CreateHostelRequest request);

    String bulkCreateRooms(BulkRoomCreateRequest request);

    String assignAdminToHostel(String hostelCode, String username);

    List<Hostel> getAllHostels();

    Hostel getHostelById(Long id);
}