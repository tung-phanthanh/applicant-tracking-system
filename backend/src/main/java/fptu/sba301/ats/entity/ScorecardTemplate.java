package fptu.sba301.ats.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scorecard_templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScorecardTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "department_id")
    private Long departmentId;

    /** Soft-archive flag – archived templates are hidden from default listing */
    @Column(name = "archived", nullable = false)
    @Builder.Default
    private boolean archived = false;

    /** Optimistic-locking version – auto-incremented on every update */
    @Column(name = "version", nullable = false)
    @Builder.Default
    private Integer version = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("display_order ASC")
    @Builder.Default
    private List<ScorecardCriterion> criteria = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (this.version == null)
            this.version = 1;
        this.version = this.version + 1;
    }
}
