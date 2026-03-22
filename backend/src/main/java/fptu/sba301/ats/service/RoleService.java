package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.RoleRequestDTO;
import fptu.sba301.ats.dto.response.RoleResponseDTO;
import fptu.sba301.ats.entity.Permission;

import java.util.List;

import java.util.UUID;

public interface RoleService {
    List<RoleResponseDTO> getAllRoles();
    List<Permission> getAllPermissions();

    RoleResponseDTO createRole(RoleRequestDTO request);

    RoleResponseDTO updateRole(UUID id, RoleRequestDTO request);

    void deleteRole(UUID id);
}
