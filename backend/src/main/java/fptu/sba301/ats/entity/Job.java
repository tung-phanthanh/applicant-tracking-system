package fptu.sba301.ats.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

/**
 * Represents a job posting within the ATS.
 */
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "jobs")
public class Job extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "department")
    private UUID department;

    @Column(name = "location")
    private String location;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "posted_date")
    private Instant postedDate;
}
