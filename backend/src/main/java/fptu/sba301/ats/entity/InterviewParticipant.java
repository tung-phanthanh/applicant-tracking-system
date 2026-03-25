package fptu.sba301.ats.entity;

import fptu.sba301.ats.enums.ParticipantRole;
import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "interview_participants",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"interview_id", "user_id"}
        )
)
public class InterviewParticipant {

    @EmbeddedId
    private InterviewParticipantId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("interviewId")
    @JoinColumn(name = "interview_id")
    private Interview interview;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    private ParticipantRole role;

    private String feedback;
    private BigDecimal overallScore;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class InterviewParticipantId implements Serializable {
        private UUID interviewId;
        private UUID userId;
    }
}