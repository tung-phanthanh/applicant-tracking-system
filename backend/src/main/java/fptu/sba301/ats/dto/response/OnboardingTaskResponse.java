package fptu.sba301.ats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingTaskResponse {
    private UUID id;
    private UUID applicationId;
    private String title;
    private String category;
    private String assignedTo;
    private LocalDate dueDate;
    private boolean completed;
    private int sortOrder;
}
