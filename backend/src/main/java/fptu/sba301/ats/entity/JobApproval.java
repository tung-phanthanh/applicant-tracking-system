package fptu.sba301.ats.entity;

import fptu.sba301.ats.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "job_approvals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "job_id", nullable = false)
    private Long jobId;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private ApprovalStatus status;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "approved_at", nullable = false, updatable = false)
    private Instant approvedAt;

    @PrePersist
    protected void onCreate() {
        this.approvedAt = Instant.now();
    }
}
