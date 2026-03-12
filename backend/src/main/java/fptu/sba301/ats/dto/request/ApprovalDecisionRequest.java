package fptu.sba301.ats.dto.request;

import jakarta.validation.constraints.Size;

public record ApprovalDecisionRequest(

        @Size(max = 1000, message = "Comment must not exceed 1000 characters") String comment

) {
}
