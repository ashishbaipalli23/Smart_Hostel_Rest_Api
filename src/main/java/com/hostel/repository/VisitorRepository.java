package com.hostel.repository;

import com.hostel.models.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByTenantId(Long tenantId);
    List<Visitor> findByStatus(String status);
}
