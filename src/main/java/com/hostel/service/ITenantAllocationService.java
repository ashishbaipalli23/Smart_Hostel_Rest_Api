package com.hostel.service;

import com.hostel.web.request.AllocateTenantRequest;

public interface ITenantAllocationService {
    public String allocateTenant(AllocateTenantRequest request);
}
