package fptu.sba301.ats.controller;

import fptu.sba301.ats.constant.AdminConstants;
import fptu.sba301.ats.constant.AppConstant;
import fptu.sba301.ats.constant.PermissionConstants;
import fptu.sba301.ats.dto.response.AuditLogResponseDTO;
import fptu.sba301.ats.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(AppConstant.BASE_URL + AdminConstants.AUDIT_LOG_URL)
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    @PreAuthorize("hasAuthority('" + PermissionConstants.AUDIT_LOG_VIEW + "')")
    public ResponseEntity<List<AuditLogResponseDTO>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/action")
    @PreAuthorize("hasAuthority('" + PermissionConstants.AUDIT_LOG_VIEW + "')")
    public ResponseEntity<List<AuditLogResponseDTO>> getLogsByAction(@RequestParam String action) {
        return ResponseEntity.ok(auditLogService.getLogsByAction(action));
    }
}
