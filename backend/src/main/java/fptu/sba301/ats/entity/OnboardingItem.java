package fptu.sba301.ats.entity;

import fptu.sba301.ats.enums.ChecklistItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "onboarding_items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checklist_id", nullable = false)
    private OnboardingChecklist checklist;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ChecklistItemStatus status;

    @Column(name = "assigned_to")
    private Long assignedTo;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "display_order")
    private Integer displayOrder;

    @PrePersist
    protected void onCreate() {
        if (this.status == null) {
            this.status = ChecklistItemStatus.PENDING;
        }
    }
}
