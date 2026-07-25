package com.hostel.repository;

import com.hostel.models.Hostel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HostelRepository extends JpaRepository<Hostel, Long> {

    Optional<Hostel> findByCode(String code);

    boolean existsByCode(String code);
}