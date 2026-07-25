package com.hostel.repository;

import com.hostel.models.AdminHostelMapping;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminHostelMappingRepository extends JpaRepository<AdminHostelMapping,Long> {

    boolean existsByAdminIdAndHostelId( long adminId, long hostelId);
}
