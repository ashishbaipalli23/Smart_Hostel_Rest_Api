package com.hostel.utils;

import com.hostel.enums.Roles;
import com.hostel.security.UserPrincipal;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;


public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Authentication getAuthentication() {

        return SecurityContextHolder
                .getContext()
                .getAuthentication();
    }

    public static boolean isAuthenticated() {

        Authentication auth = getAuthentication();

        return auth != null
                && auth.isAuthenticated()
                && !(auth instanceof AnonymousAuthenticationToken);
    }


    public static String getCurrentUsername() {

        Authentication auth = getAuthentication();

        if (auth == null) {
            throw new RuntimeException(
                    "No authenticated user found"
            );
        }

        return auth.getName();
    }

    public static Long getCurrentUserId() {
        UserPrincipal user = getPrincipal();
        return user.getUserId();
    }


    public static UserPrincipal getPrincipal() {
        Authentication auth = getAuthentication();
        return (UserPrincipal) auth.getPrincipal();
    }

    public static Roles getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority().replace("ROLE_", ""))
                .map(Roles::valueOf)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No role found"));
    }


}
