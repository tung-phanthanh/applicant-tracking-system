package fptu.sba301.ats.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RoleRequestDTO {
    private String name;
    private String description;
    private List<String> permissionKeys;
}
