package fptu.sba301.ats.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateOnboardingTaskRequest {
    private String title;
    private String category;
    private String assignedTo;
    private LocalDate dueDate;
    private Boolean completed;
}
