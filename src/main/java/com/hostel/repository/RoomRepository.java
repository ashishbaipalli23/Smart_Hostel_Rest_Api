package com.hostel.repository;

import com.hostel.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    boolean existsByHostelIdAndRoomNumber(Long hostelId, String roomNumber);

    List<Room> findByHostelId(Long hostelId);
}