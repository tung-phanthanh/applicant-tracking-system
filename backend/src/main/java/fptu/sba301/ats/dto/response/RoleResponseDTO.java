package fptu.sba301.ats.dto.response;

import lombok.Data;
import lombok.Builder;
import fptu.sba301.ats.entity.Permission;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
public class RoleResponseDTO {
    private UUID id;
    private String name;
    private String description;
    private boolean isSystemRole;
    private Set<Permission> permissions;
}
