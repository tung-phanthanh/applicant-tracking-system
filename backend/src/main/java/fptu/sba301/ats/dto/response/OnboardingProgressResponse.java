package fptu.sba301.ats.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingProgressResponse {
    private int totalTasks;
    private int completedTasks;
    private double progressPercent;
    private List<OnboardingTaskResponse> tasks;
}
