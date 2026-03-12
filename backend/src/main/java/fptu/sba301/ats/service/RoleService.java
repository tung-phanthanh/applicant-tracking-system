package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.RoleRequestDTO;
import fptu.sba301.ats.dto.response.RoleResponseDTO;

import java.util.List;

import java.util.UUID;

public interface RoleService {
    List<RoleResponseDTO> getAllRoles();

    RoleResponseDTO createRole(RoleRequestDTO request);

    RoleResponseDTO updateRole(UUID id, RoleRequestDTO request);

    void deleteRole(UUID id);
}
