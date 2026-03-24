package fptu.sba301.ats.controller;

import fptu.sba301.ats.constant.AdminConstants;
import fptu.sba301.ats.constant.AppConstant;
import fptu.sba301.ats.constant.PermissionConstants;
import fptu.sba301.ats.dto.response.NotificationResponseDTO;
import fptu.sba301.ats.security.UserPrincipal;
import fptu.sba301.ats.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(AppConstant.BASE_URL + AdminConstants.NOTIFICATION_URL)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ========== USER NOTIFICATIONS (Personal Inbox) ==========

    @GetMapping(AppConstant.BASE_URL + "/notifications")
    public ResponseEntity<List<NotificationResponseDTO>> getUserNotifications(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) Boolean unreadOnly) {
        List<NotificationResponseDTO> responses = notificationService
                .getAllMyNotifications(principal.getEmail(), unreadOnly)
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

    @GetMapping(AppConstant.BASE_URL + "/notifications/unread-count")
    public ResponseEntity<Long> getUnreadCount(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(notificationService.getUnreadCount(principal.getEmail()));
    }

    @PatchMapping(AppConstant.BASE_URL + "/notifications/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable String id, @AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAsRead(Long.valueOf(id), principal.getEmail());
        return ResponseEntity.ok().build();
    }

    @PatchMapping(AppConstant.BASE_URL + "/notifications/read-all")
    public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.markAllAsRead(principal.getEmail());
        return ResponseEntity.ok().build();
    }

    @PostMapping(AppConstant.BASE_URL + "/notifications/test-data")
    public ResponseEntity<Void> createTestNotification(@AuthenticationPrincipal UserPrincipal principal) {
        notificationService.createNotification(
                principal.getId(),
                fptu.sba301.ats.enums.NotificationType.INTERVIEW_PENDING,
                "Welcome to ATS",
                "This is a test notification to verify the feature is working properly.",
                null);
        return ResponseEntity.ok().build();
    }

    // ========== ADMIN NOTIFICATION CENTER (Bulk Send) ==========

    /**
     * Admin: Send bulk notification to users/roles
     * 
     * Request body: {
     * "title": "System Maintenance",
     * "message": "System will be down for maintenance...",
     * "roleIds": ["role-uuid-1", "role-uuid-2"], // Optional: by roles
     * "userIds": ["user-uuid-1", "user-uuid-2"] // Optional: by users
     * }
     */
    @PostMapping(AppConstant.BASE_URL + AdminConstants.NOTIFICATION_URL + "/admin/send")
    @PreAuthorize("hasAuthority('" + PermissionConstants.NOTIFICATION_MANAGE + "')")
    public ResponseEntity<Map<String, Object>> sendBulkNotification(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody Map<String, Object> request) {
        String title = (String) request.get("title");
        String message = (String) request.get("message");

        // TODO: Implement bulk notification logic
        // - Send to roles if roleIds provided
        // - Send to users if userIds provided
        // - Log the notification send event

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Notification queued for sending",
                "timestamp", System.currentTimeMillis()));
    }

    /**
     * Admin: Get notification send history
     */
    @GetMapping(AppConstant.BASE_URL + AdminConstants.NOTIFICATION_URL + "/admin/history")
    @PreAuthorize("hasAuthority('" + PermissionConstants.NOTIFICATION_MANAGE + "')")
    public ResponseEntity<List<Map<String, Object>>> getNotificationHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // TODO: Implement history pagination
        return ResponseEntity.ok(List.of());
    }

    /**
     * Admin: Get notification statistics
     */
    @GetMapping(AppConstant.BASE_URL + AdminConstants.NOTIFICATION_URL + "/admin/stats")
    @PreAuthorize("hasAuthority('" + PermissionConstants.NOTIFICATION_MANAGE + "')")
    public ResponseEntity<Map<String, Object>> getNotificationStats() {
        // TODO: Implement notification statistics
        return ResponseEntity.ok(Map.of(
                "totalSent", 0,
                "delivered", 0,
                "failed", 0,
                "pending", 0));
    }
}
