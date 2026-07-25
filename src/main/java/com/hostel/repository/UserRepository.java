package com.hostel.repository;

import com.hostel.enums.Roles;
import com.hostel.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUsernameOrAadhaarNumber(String username, String aadhaarNumber);

    Optional<UserEntity> findByUsername(String username);

    Optional<UserEntity> findByUsernameAndRole(String username, Roles role);

    List<UserEntity> findByRole(Roles role);
}
