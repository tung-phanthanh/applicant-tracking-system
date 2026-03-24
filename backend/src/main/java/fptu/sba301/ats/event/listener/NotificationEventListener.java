package fptu.sba301.ats.event.listener;

import fptu.sba301.ats.entity.Notification;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.event.SystemEvent;
import fptu.sba301.ats.repository.NotificationRepository;
import fptu.sba301.ats.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

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

        // For system config updates and similar global events, we notify all active
        // admin users.
        // In this implementation, let's assume we fetch all users with the "Admin"
        // role.
        // Or temporarily, all active users for testing purposes.
        List<User> users = userRepository.findAll(); // Optimization: filter by role in DB

        for (User user : users) {
            Notification notification = Notification.builder()
                    .userId(user.getId())
                    .title(event.getTitle())
                    .content(event.getContent())
                    .type(event.getType())
                    .link(event.getLink())
                    .isRead(false)
                    .build();
            notificationRepository.save(java.util.Objects.requireNonNull(notification));

        }

        log.info("Notifications dispatched for SystemEvent");
    }
}
