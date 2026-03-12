package fptu.sba301.ats.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "interview_scores", uniqueConstraints = @UniqueConstraint(name = "uq_interview_interviewer_criterion", columnNames = {
        "interview_id", "interviewer_id", "criterion_id" }))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "interview_id", nullable = false)
    private Long interviewId;

    @Column(name = "interviewer_id", nullable = false)
    private Long interviewerId;

    @Column(name = "criterion_id", nullable = false)
    private Long criterionId;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "overall_comment", columnDefinition = "TEXT")
    private String overallComment;

    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;

    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation", length = 50)
    private fptu.sba301.ats.enums.Recommendation recommendation;

    @Column(name = "submitted_at", nullable = false, updatable = false)
    private Instant submittedAt;

    @PrePersist
    protected void onCreate() {
        this.submittedAt = Instant.now();
    }
}
