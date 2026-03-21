package fptu.sba301.ats.entity;

import fptu.sba301.ats.enums.ParticipantRole;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interview_participants")
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
    @Column(name = "role", nullable = false)
    private ParticipantRole role;
    
    @Column(name = "feedback", columnDefinition = "TEXT")
    private String feedback;
    
    @Column(name = "overall_score")
    private Integer overallScore;

    @Column(name = "overall_comment", columnDefinition = "TEXT")
    private String overallComment;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation")
    private fptu.sba301.ats.enums.Recommendation recommendation;

    @Column(name = "submitted_at")
    private java.time.Instant submittedAt;

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class InterviewParticipantId implements Serializable {
        @Column(name = "interview_id")
        private UUID interviewId;

        @Column(name = "user_id")
        private UUID userId;
    }
}