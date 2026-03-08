package fptu.sba301.ats.entity;

import fptu.sba301.ats.enums.ApprovalStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "offer_approvals")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferApproval {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    @Column(name = "approved_by", nullable = false)
    private Long approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
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
