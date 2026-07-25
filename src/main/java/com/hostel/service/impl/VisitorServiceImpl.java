package com.hostel.service.impl;

import com.hostel.exceptions.ResourceNotFoundException;
import com.hostel.models.UserEntity;
import com.hostel.models.Visitor;
import com.hostel.repository.UserRepository;
import com.hostel.repository.VisitorRepository;
import com.hostel.service.IVisitorService;
import com.hostel.web.request.RegisterVisitorRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements IVisitorService {

    private final VisitorRepository visitorRepository;
    private final UserRepository userRepository;

    @Override
    public Visitor registerVisitor(RegisterVisitorRequest request) {
        UserEntity tenant = userRepository.findById(request.getTenantId())
                .orElseThrow(() -> new ResourceNotFoundException("Tenant not found"));

        Visitor visitor = Visitor.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .purpose(request.getPurpose() != null ? request.getPurpose() : "Guest Visit")
                .tenant(tenant)
                .roomNumber(request.getRoomNumber() != null ? request.getRoomNumber() : "A-101")
                .entryTime(LocalDateTime.now())
                .status("INSIDE")
                .build();

        return visitorRepository.save(visitor);
    }

    @Override
    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll();
    }

    @Override
    public Visitor checkoutVisitor(Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor entry not found: " + id));

        visitor.setExitTime(LocalDateTime.now());
        visitor.setStatus("CHECKED_OUT");
        return visitorRepository.save(visitor);
    }
}
