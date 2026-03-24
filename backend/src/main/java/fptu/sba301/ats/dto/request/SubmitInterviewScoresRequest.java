package fptu.sba301.ats.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record SubmitInterviewScoresRequest(

                @NotEmpty(message = "At least one score is required") List<@Valid SubmitScoreItemRequest> scores,

                @Size(max = 2000, message = "Overall comment must not exceed 2000 characters") String overallComment,

                @Size(max = 2000, message = "Strengths must not exceed 2000 characters") String strengths,

                @Size(max = 2000, message = "Weaknesses must not exceed 2000 characters") String weaknesses,

                fptu.sba301.ats.enums.Recommendation recommendation) {
}
