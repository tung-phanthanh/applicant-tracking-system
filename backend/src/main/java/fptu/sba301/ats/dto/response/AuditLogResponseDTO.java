package fptu.sba301.ats.dto.response;

import lombok.Data;
import lombok.Builder;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class AuditLogResponseDTO {
    private UUID id;
    private UUID userId;
    private String userEmail;
    private String userFullName;
    private String action;
    private String entityType;
    private UUID entityId;
    private Object oldValue;
    private Object newValue;
    private String ipAddress;
    private String userAgent;
    private Instant createdAt;
}
