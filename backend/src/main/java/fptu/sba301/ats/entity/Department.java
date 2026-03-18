package fptu.sba301.ats.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "departments")
public class Department {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    // ── Bidirectional back-refs ──

    @OneToMany(mappedBy = "department")
    @Builder.Default
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "department")
    @Builder.Default
    private List<Job> jobs = new ArrayList<>();

    @OneToMany(mappedBy = "department")
    @Builder.Default
    private List<ScorecardTemplate> scorecardTemplates = new ArrayList<>();

    @jakarta.persistence.PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
