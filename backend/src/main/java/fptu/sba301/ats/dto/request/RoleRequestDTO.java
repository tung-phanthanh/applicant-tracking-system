package fptu.sba301.ats.dto.request;

import fptu.sba301.ats.enums.Role;
import lombok.Data;
import java.util.List;

@Data
public class RoleRequestDTO {
    private Role name;
    private String description;
    private List<String> permissionKeys;
}
