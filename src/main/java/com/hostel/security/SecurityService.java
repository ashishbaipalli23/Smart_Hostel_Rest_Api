package com.hostel.security;

import com.hostel.enums.Roles;
import com.hostel.repository.AdminHostelMappingRepository;
import com.hostel.repository.HostelRepository;
import com.hostel.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final AdminHostelMappingRepository adminHostelMappingRepository;

    private final HostelRepository hostelRepository;


    public void validateHostelAccess(String hostelCode,Long hostelId) {

        Long currentUserId = SecurityUtils.getCurrentUserId();

        Roles currentRole = SecurityUtils.getCurrentUserRole();

        // SUPER_ADMIN bypass
        if (currentRole == Roles.SUPER_ADMIN) {
            return;
        }
        if (currentRole != Roles.ADMIN) {

            throw new AccessDeniedException(
                    "Only admins can access hostel resources"
            );
        }

//        Long hostelId = hostelRepository.findByCode(hostelCode)
//                .orElseThrow(() -> new ResourceNotFoundException("Hostel not found"))
//                .getId();

        boolean assigned = adminHostelMappingRepository.existsByAdminIdAndHostelId(currentUserId, hostelId);

        if (!assigned) {

            throw new AccessDeniedException("You are not authorized for this hostel");
        }
    }
}