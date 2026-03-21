package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.response.AuditLogResponseDTO;

import java.util.List;
import java.util.Map;

import java.util.UUID;

public interface AuditLogService {
    List<AuditLogResponseDTO> getAllLogs();

    List<AuditLogResponseDTO> getLogsByAction(String action);

    void logAction(UUID userId, String action, String entityType, String entityId,
            Map<String, Object> oldValue, Map<String, Object> newValue,
            String ipAddress, String userAgent);
}
