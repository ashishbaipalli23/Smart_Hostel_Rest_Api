package com.hostel.service;

import com.hostel.models.Notification;
import java.util.List;

public interface INotificationService {
    List<Notification> getUserNotifications(Long userId);
    Notification markAsRead(Long notificationId);
}
