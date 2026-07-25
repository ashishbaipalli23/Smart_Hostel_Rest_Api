package com.hostel.service;

import com.hostel.models.Visitor;
import com.hostel.web.request.RegisterVisitorRequest;
import java.util.List;

public interface IVisitorService {
    Visitor registerVisitor(RegisterVisitorRequest request);
    List<Visitor> getAllVisitors();
    Visitor checkoutVisitor(Long id);
}
