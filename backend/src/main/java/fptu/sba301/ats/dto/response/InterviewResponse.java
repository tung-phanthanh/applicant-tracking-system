package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.enums.InterviewType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewResponse {
    private UUID id;
    private Instant scheduledAt;
    private Instant startedAt;
    private Instant endedAt;
    private String location;
    private String meetingLink;
    private InterviewType type;
    private InterviewStatus status;

    private UUID applicationId;
    private UUID templateId;

    private Integer participantCount;
    private Integer scoreCount;
}