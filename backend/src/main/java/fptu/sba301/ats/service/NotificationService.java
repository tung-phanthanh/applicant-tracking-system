package fptu.sba301.ats.service;

import fptu.sba301.ats.entity.Notification;
import fptu.sba301.ats.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    void createNotification(Long userId, NotificationType type, String title, String message, Long referenceId);

    Page<Notification> getMyNotifications(String email, Pageable pageable);

    void markAsRead(Long notificationId, String email);
}
