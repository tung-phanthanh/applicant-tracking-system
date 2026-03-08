package fptu.sba301.ats.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "jobs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "department_id")
    private Long departmentId;

    @Column(name = "hiring_manager_id")
    private Long hiringManagerId;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "headcount")
    private Integer headcount;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;
}
