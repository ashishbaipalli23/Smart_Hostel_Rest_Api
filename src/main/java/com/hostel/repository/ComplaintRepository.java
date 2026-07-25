package com.hostel.repository;

import com.hostel.models.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByTenantId(Long tenantId);
    List<Complaint> findByAssignedStaffId(Long staffId);
    List<Complaint> findByStatus(String status);
    long countByStatus(String status);
}
