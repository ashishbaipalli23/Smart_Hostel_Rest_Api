package com.hostel.controller;

import com.hostel.models.Notification;
import com.hostel.service.INotificationService;
import com.hostel.web.response.APIResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_TENANT', 'ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<APIResponse<List<Notification>>> getUserNotifications(@RequestParam(required = false) Long userId) {
        Long id = userId != null ? userId : 1L;
        List<Notification> list = notificationService.getUserNotifications(id);
        APIResponse<List<Notification>> apiResponse = APIResponse.<List<Notification>>builder()
                .timeStamp(LocalDateTime.now())
                .msg("User notifications fetched")
                .data(list)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('ROLE_TENANT', 'ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<APIResponse<Notification>> markAsRead(@PathVariable Long id) {
        Notification notification = notificationService.markAsRead(id);
        APIResponse<Notification> apiResponse = APIResponse.<Notification>builder()
                .timeStamp(LocalDateTime.now())
                .msg("Notification marked as read")
                .data(notification)
                .status(HttpStatus.OK.value())
                .build();
        return ResponseEntity.ok(apiResponse);
    }
}
