package fptu.sba301.ats.dto.response;

import lombok.Data;
import lombok.Builder;
import java.util.UUID;

@Data
@Builder
public class DepartmentResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private String createdAt;
    private String updatedAt;
}
