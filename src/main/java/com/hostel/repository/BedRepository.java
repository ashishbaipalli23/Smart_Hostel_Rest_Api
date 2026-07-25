package com.hostel.repository;

import com.hostel.enums.BedStatus;
import com.hostel.models.Bed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BedRepository extends JpaRepository<Bed, Long> {

    List<Bed> findByStatus(BedStatus status);

    long countByStatus(BedStatus status);

    List<Bed> findByRoomId(Long roomId);

    Optional<Bed> findByRoom_Hostel_CodeAndBedNumber(String hostelCode, String bedNumber);

    @Query("""
                SELECT b
                FROM Bed b
                WHERE b.room.hostel.code = :hostelCode
                AND b.bedNumber = :bedNumber
            """)
    Optional<Bed> findBed(String hostelCode, String bedNumber);
}