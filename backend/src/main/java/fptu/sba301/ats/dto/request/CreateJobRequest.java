package fptu.sba301.ats.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateJobRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must be at most 255 characters")
    private String title;

    @Size(max = 10000, message = "Description is too long")
    private String description;

    @Size(max = 255, message = "Location must be at most 255 characters")
    private String location;

    @Size(max = 255, message = "Salary must be at most 255 characters")
    private String salary;

    private UUID departmentId;

    /** Resolved to a department when departmentId is null */
    @Size(max = 255, message = "Department name is too long")
    private String departmentName;

    private Integer headcount;
}
