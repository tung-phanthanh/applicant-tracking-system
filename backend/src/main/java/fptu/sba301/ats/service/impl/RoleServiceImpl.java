package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.dto.request.RoleRequestDTO;
import fptu.sba301.ats.dto.response.RoleResponseDTO;
import fptu.sba301.ats.entity.Permission;
import fptu.sba301.ats.entity.Role;
import fptu.sba301.ats.repository.PermissionRepository;
import fptu.sba301.ats.repository.RoleRepository;
import fptu.sba301.ats.service.RoleService;
import lombok.RequiredArgsConstructor;
import fptu.sba301.ats.annotation.LogAudit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import fptu.sba301.ats.exception.BusinessException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    @Override
    @Transactional
    @LogAudit(action = "CREATE", resource = "Role")
    public RoleResponseDTO createRole(RoleRequestDTO request) {
        if (roleRepository.existsByName(request.getName())) {
            throw new BusinessException("Role name already exists", HttpStatus.BAD_REQUEST);
        }

        Role role = new Role();
        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setSystemRole(false);

        if (request.getPermissionKeys() != null && !request.getPermissionKeys().isEmpty()) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionKeys());
            role.setPermissions(new HashSet<>(permissions));
        }

        Role saved = roleRepository.save(role);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    @LogAudit(action = "UPDATE", resource = "Role")
    public RoleResponseDTO updateRole(UUID id, RoleRequestDTO request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Role not found", HttpStatus.NOT_FOUND));

        if (role.isSystemRole()) {
            throw new BusinessException("Cannot modify system roles", HttpStatus.FORBIDDEN);
        }

        if (role.getName() != request.getName() && roleRepository.existsByName(request.getName())) {
            throw new BusinessException("Role name already exists", HttpStatus.BAD_REQUEST);
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());

        if (request.getPermissionKeys() != null) {
            List<Permission> permissions = permissionRepository.findAllById(request.getPermissionKeys());
            role.setPermissions(new HashSet<>(permissions));
        }

        Role updated = roleRepository.save(role);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    @LogAudit(action = "DELETE", resource = "Role")
    public void deleteRole(UUID id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Role not found", HttpStatus.NOT_FOUND));

        if (role.isSystemRole()) {
            throw new BusinessException("Cannot delete system roles", HttpStatus.FORBIDDEN);
        }

        roleRepository.delete(role);
    }

    private RoleResponseDTO mapToDTO(Role role) {
        return RoleResponseDTO.builder()
                .id(role.getId())
                .name(role.getName().name())
                .description(role.getDescription())
                .isSystemRole(role.isSystemRole())
                .permissions(role.getPermissions())
                .build();
    }
}
