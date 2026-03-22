package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.entity.Notification;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.NotificationType;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.NotificationRepository;
import fptu.sba301.ats.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;

    private User user;
    private String userEmail;

    @BeforeEach
    void setUp() {
        userEmail = "test@ats.com";
        user = User.builder()
                .id(UUID.randomUUID())
                .email(userEmail)
                .fullName("Test User")
                .build();
    }

    @Test
    void testCreateNotification_Success() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> {
            Notification n = inv.getArgument(0);
            n.setId(1L);
            return n;
        });

        notificationService.createNotification(
                user.getId(),
                NotificationType.INTERVIEW_PENDING,
                "Interview Scheduled",
                "You have an interview tomorrow",
                100L
        );

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository, times(1)).save(captor.capture());

        Notification saved = captor.getValue();
        assertEquals(user.getId(), saved.getUserId());
        assertEquals(NotificationType.INTERVIEW_PENDING, saved.getType());
        assertEquals("Interview Scheduled", saved.getTitle());
        assertEquals("You have an interview tomorrow", saved.getMessage());
        assertEquals(100L, saved.getReferenceId());
        assertFalse(saved.isRead());
    }

    @Test
    void testGetMyNotifications_Success() {
        Notification notif = Notification.builder()
                .id(1L)
                .userId(user.getId())
                .type(NotificationType.OFFER_APPROVED)
                .title("Offer Approved")
                .message("Your offer has been approved")
                .isRead(false)
                .createdAt(Instant.now())
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        Page<Notification> page = new PageImpl<>(List.of(notif), pageable, 1);

        when(userRepository.findByEmailAndDeletedFalse(userEmail)).thenReturn(Optional.of(user));
        when(notificationRepository.findByUserId(eq(user.getId()), any(Pageable.class))).thenReturn(page);

        Page<Notification> result = notificationService.getMyNotifications(userEmail, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Offer Approved", result.getContent().get(0).getTitle());
    }

    @Test
    void testMarkAsRead_Success() {
        Notification notification = Notification.builder()
                .id(1L)
                .userId(user.getId())
                .type(NotificationType.OFFER_APPROVED)
                .title("Offer Approved")
                .message("Test")
                .isRead(false)
                .build();

        when(userRepository.findByEmailAndDeletedFalse(userEmail)).thenReturn(Optional.of(user));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        notificationService.markAsRead(1L, userEmail);

        assertTrue(notification.isRead());
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void testMarkAsRead_NotFound_ThrowsException() {
        when(userRepository.findByEmailAndDeletedFalse(userEmail)).thenReturn(Optional.of(user));
        when(notificationRepository.findById(999L)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            notificationService.markAsRead(999L, userEmail);
        });

        assertEquals("Notification not found", ex.getMessage());
    }

    @Test
    void testMarkAsRead_UnauthorizedUser_ThrowsException() {
        // Notification belongs to a different user
        Notification notification = Notification.builder()
                .id(1L)
                .userId(UUID.randomUUID()) // Different user
                .type(NotificationType.OFFER_APPROVED)
                .title("Offer Approved")
                .message("Test")
                .isRead(false)
                .build();

        when(userRepository.findByEmailAndDeletedFalse(userEmail)).thenReturn(Optional.of(user));
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            notificationService.markAsRead(1L, userEmail);
        });

        assertEquals("Not authorized to read this notification", ex.getMessage());
        verify(notificationRepository, never()).save(any());
    }

    @Test
    void testGetMyNotifications_UserNotFound_ThrowsException() {
        when(userRepository.findByEmailAndDeletedFalse("unknown@ats.com")).thenReturn(Optional.empty());

        Pageable pageable = PageRequest.of(0, 10);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            notificationService.getMyNotifications("unknown@ats.com", pageable);
        });

        assertTrue(ex.getMessage().contains("User not found"));
    }
}
