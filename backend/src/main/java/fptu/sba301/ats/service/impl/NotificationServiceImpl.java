package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.entity.Notification;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.NotificationType;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.NotificationRepository;
import fptu.sba301.ats.repository.UserRepository;
import fptu.sba301.ats.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createNotification(Long userId, NotificationType type, String title, String message, Long referenceId) {
        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .title(title)
                .message(message)
                .referenceId(referenceId)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
        log.info("Created notification for user {}, type: {} - {}", userId, type, title);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Notification> getMyNotifications(String email, Pageable pageable) {
        User user = findUserOrThrow(email);
        Long userLongId = resolveUserLongId(user);
        return notificationRepository.findByUserId(userLongId, pageable);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, String email) {
        User user = findUserOrThrow(email);
        Long userLongId = resolveUserLongId(user);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new BusinessException("Notification not found", HttpStatus.NOT_FOUND));

        if (!notification.getUserId().equals(userLongId)) {
            throw new BusinessException("Not authorized to read this notification", HttpStatus.FORBIDDEN);
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private User findUserOrThrow(String email) {
        return userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new BusinessException("User not found with email: " + email, HttpStatus.NOT_FOUND));
    }

    // See SecurityUtils for comments on the UUID/Long PK mismatch.
    private Long resolveUserLongId(User user) {
        return (long) user.getEmail().hashCode();
    }
}
