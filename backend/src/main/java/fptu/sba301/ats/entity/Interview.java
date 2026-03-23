package fptu.sba301.ats.entity;

import fptu.sba301.ats.enums.InterviewStatus;
import fptu.sba301.ats.enums.InterviewType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interviews")
public class Interview extends BaseEntity {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private ScorecardTemplate template;

    private Instant scheduledAt;
    private Instant startedAt;
    private Instant endedAt;

    private String location;
    private String meetingLink;

    @Enumerated(EnumType.STRING)
    private InterviewType type;

    @Enumerated(EnumType.STRING)
    private InterviewStatus status = InterviewStatus.SCHEDULED;
    
    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
    private List<InterviewParticipant> participants;

    @OneToMany(mappedBy = "interview", cascade = CascadeType.ALL)
    private List<InterviewScore> scores;
}