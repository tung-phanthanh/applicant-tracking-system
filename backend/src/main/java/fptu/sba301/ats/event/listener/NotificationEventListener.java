package fptu.sba301.ats.event.listener;

import fptu.sba301.ats.entity.Notification;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.NotificationType;
import fptu.sba301.ats.event.SystemEvent;
import fptu.sba301.ats.repository.NotificationRepository;
import fptu.sba301.ats.repository.UserRepository;
import fptu.sba301.ats.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationEventListener {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Async
    @EventListener
    public void handleSystemEvent(SystemEvent event) {
        log.info("Received SystemEvent: {} - {}", event.getTitle(), event.getContent());

        UUID currentUserId = getCurrentUserId();

        NotificationType notificationType = resolveType(event.getType());
        String message = buildMessage(event);

        List<User> users = userRepository.findByDeletedFalse();

        int sentCount = 0;
        for (User user : users) {
            if (currentUserId != null && user.getId().equals(currentUserId)) {
                continue;
            }

            Notification notification = Notification.builder()
                    .userId(user.getId())
                    .title(event.getTitle())
                    .message(message)
                    .type(notificationType)
                    .isRead(false)
                    .build();
            notificationRepository.save(notification);
            sentCount++;
        }

        log.info("Notifications dispatched to {} other users for SystemEvent", sentCount);
    }

    private UUID getCurrentUserId() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof UserPrincipal principal) {
                return principal.getId();
            }
        } catch (Exception e) {
            log.debug("Could not determine current user for event filtering: {}", e.getMessage());
        }
        return null;
    }

    private static NotificationType resolveType(String type) {
        if (type == null || type.isBlank()) {
            return NotificationType.SYSTEM_ALERT;
        }
        try {
            return NotificationType.valueOf(type.trim());
        } catch (IllegalArgumentException ex) {
            return NotificationType.SYSTEM_ALERT;
        }
    }

    private static String buildMessage(SystemEvent event) {
        String content = event.getContent() != null ? event.getContent() : "";
        if (event.getLink() != null && !event.getLink().isBlank()) {
            return content + "\n" + event.getLink();
        }
        return content;
    }
}
