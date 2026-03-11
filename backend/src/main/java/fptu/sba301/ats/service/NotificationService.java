package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.NotificationResponseDTO;

import java.util.List;

import java.util.UUID;

public interface NotificationService {
    List<NotificationResponseDTO> getUserNotifications(UUID userId, Boolean unreadOnly);

    long getUnreadCount(UUID userId);

    void markAsRead(UUID id);

    void markAllAsRead(UUID userId);
}
