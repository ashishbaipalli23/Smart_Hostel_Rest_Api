package com.hostel.repository;

import com.hostel.enums.AllocationStatus;
import com.hostel.models.TenantAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TenantAllocationRepository extends JpaRepository<TenantAllocation, Long> {

    Optional<TenantAllocation> findByTenantIdAndAllocationStatus(Long tenantId, AllocationStatus status);

    Optional<TenantAllocation> findByBedIdAndAllocationStatus(Long bedId, AllocationStatus status);

    boolean existsByTenantIdAndAllocationStatus(Long tenantId, AllocationStatus allocationStatus);

    boolean existsByBedIdAndAllocationStatus(Long bedId, AllocationStatus allocationStatus);
}