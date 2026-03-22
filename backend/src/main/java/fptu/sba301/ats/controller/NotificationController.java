package fptu.sba301.ats.controller;

import fptu.sba301.ats.constant.AppConstant;
import fptu.sba301.ats.dto.response.NotificationResponseDTO;
import fptu.sba301.ats.security.UserPrincipal;
import fptu.sba301.ats.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstant.BASE_URL + "/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Boolean unreadOnly) {
        List<NotificationResponseDTO> responses = notificationService.getAllMyNotifications(principal.getEmail(), unreadOnly)
                .stream()
                .map(n -> NotificationResponseDTO.builder()
                        .id(n.getId().toString())
                        .title(n.getTitle())
                        .message(n.getMessage())
                        .read(n.isRead())
                        .createdAt(n.getCreatedAt())
                        .build())
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.getUnreadCount(principal.getEmail()));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id, @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAsRead(Long.valueOf(id), principal.getEmail());
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllAsRead(principal.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test-data")
    public ResponseEntity<Void> createTestNotification(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.createNotification(
                principal.getId(),
                fptu.sba301.ats.enums.NotificationType.INTERVIEW_PENDING,
                "Welcome to ATS",
                "This is a test notification to verify the feature is working properly.",
                null
        );
        return ResponseEntity.ok().build();
    }
}
