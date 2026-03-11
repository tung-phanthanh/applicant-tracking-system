package fptu.sba301.ats.dto.response;

import lombok.Data;
import lombok.Builder;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class NotificationResponseDTO {
    private UUID id;
    private String title;
    private String content;
    private String type;
    private String link;
    private boolean isRead;
    private Instant createdAt;
}
