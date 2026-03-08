package fptu.sba301.ats.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "interview_participants")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InterviewParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "interview_id", nullable = false)
    private Long interviewId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "role", length = 50)
    private String role;
}
