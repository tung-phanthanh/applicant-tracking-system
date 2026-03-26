package fptu.sba301.ats.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobDTO {

    private UUID id;
    private String title;
    private String description;
    private String location;
    private String salary;
    /** API-facing status: PENDING, APPROVED, REJECTED, DRAFT, CLOSED */
    private String status;
    private LocalDateTime createdAt;
    private UUID departmentId;
    private String departmentName;
    private Integer headcount;
}
