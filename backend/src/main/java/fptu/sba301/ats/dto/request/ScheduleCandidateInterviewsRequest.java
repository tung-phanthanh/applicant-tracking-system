package fptu.sba301.ats.dto.request;

import fptu.sba301.ats.enums.InterviewType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ScheduleCandidateInterviewsRequest {

    @NotEmpty(message = "At least one interview is required")
    @Valid
    private List<InterviewSlot> interviews;

    @Getter
    @Setter
    public static class InterviewSlot {

        @NotNull(message = "scheduledAt is required")
        private Instant scheduledAt;

        @NotNull(message = "type is required")
        private InterviewType type;

        private String location;
        private String meetingLink;

        @NotEmpty(message = "Each interview must have at least one interviewer")
        private List<UUID> interviewerIds;
    }
}
