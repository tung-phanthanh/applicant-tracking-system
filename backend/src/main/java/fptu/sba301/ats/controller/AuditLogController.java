package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.response.AuditLogResponseDTO;
import fptu.sba301.ats.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @GetMapping
    public ResponseEntity<List<AuditLogResponseDTO>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/action")
    public ResponseEntity<List<AuditLogResponseDTO>> getLogsByAction(@RequestParam String action) {
        return ResponseEntity.ok(auditLogService.getLogsByAction(action));
    }
}
