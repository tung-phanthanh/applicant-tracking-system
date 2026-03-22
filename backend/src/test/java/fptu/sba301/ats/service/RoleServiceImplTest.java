package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.RoleRequestDTO;
import fptu.sba301.ats.dto.response.RoleResponseDTO;
import fptu.sba301.ats.entity.Permission;
import fptu.sba301.ats.entity.Role;
import fptu.sba301.ats.exception.BusinessException;
import fptu.sba301.ats.repository.PermissionRepository;
import fptu.sba301.ats.repository.RoleRepository;
import fptu.sba301.ats.service.impl.RoleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    private UUID roleId;
    private Role role;

    @BeforeEach
    void setUp() {
        roleId = UUID.randomUUID();
        role = new Role();
        role.setId(roleId);
        role.setName(fptu.sba301.ats.enums.Role.HR_MANAGER);
        role.setDescription("Human Resources Manager");
        role.setSystemRole(false);

        Permission p1 = new Permission("VIEW_CANDIDATE", "View Candidates", "Recruitment");
        role.setPermissions(new HashSet<>(Set.of(p1)));
    }

    // ── GET ALL ──────────────────────────────────────────────────────────────

    @Test
    void testGetAllRoles_Success() {
        Role role2 = new Role();
        role2.setId(UUID.randomUUID());
        role2.setName(fptu.sba301.ats.enums.Role.HR);
        role2.setDescription("Recruiter Role");
        role2.setSystemRole(false);
        role2.setPermissions(new HashSet<>());

        when(roleRepository.findAll()).thenReturn(List.of(role, role2));

        List<RoleResponseDTO> result = roleService.getAllRoles();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("HR_MANAGER", result.get(0).getName());
        assertEquals("HR", result.get(1).getName());
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    @Test
    void testCreateRole_Success() {
        RoleRequestDTO request = new RoleRequestDTO();
        request.setName(fptu.sba301.ats.enums.Role.INTERVIEWER);
        request.setDescription("Interviewer Role");
        request.setPermissionKeys(List.of("VIEW_CANDIDATE"));

        Permission p1 = new Permission("VIEW_CANDIDATE", "View Candidates", "Recruitment");

        when(roleRepository.existsByName(fptu.sba301.ats.enums.Role.INTERVIEWER)).thenReturn(false);
        when(permissionRepository.findAllById(request.getPermissionKeys())).thenReturn(List.of(p1));
        when(roleRepository.save(any(Role.class))).thenAnswer(inv -> {
            Role r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            return r;
        });

        RoleResponseDTO result = roleService.createRole(request);

        assertNotNull(result);
        assertEquals("INTERVIEWER", result.getName());
        assertEquals(1, result.getPermissions().size());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testCreateRole_DuplicateName_ThrowsException() {
        RoleRequestDTO request = new RoleRequestDTO();
        request.setName(fptu.sba301.ats.enums.Role.HR_MANAGER);

        when(roleRepository.existsByName(fptu.sba301.ats.enums.Role.HR_MANAGER)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            roleService.createRole(request);
        });

        assertEquals("Role name already exists", ex.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    @Test
    void testUpdateRole_Success() {
        RoleRequestDTO request = new RoleRequestDTO();
        request.setName(fptu.sba301.ats.enums.Role.SYSTEM_ADMIN); // using a valid role enum
        request.setDescription("System Administrator");
        request.setPermissionKeys(List.of("VIEW_CANDIDATE", "EDIT_CANDIDATE"));

        Permission p1 = new Permission("VIEW_CANDIDATE", "View Candidates", "Recruitment");
        Permission p2 = new Permission("EDIT_CANDIDATE", "Edit Candidates", "Recruitment");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.existsByName(fptu.sba301.ats.enums.Role.SYSTEM_ADMIN)).thenReturn(false);
        when(permissionRepository.findAllById(request.getPermissionKeys())).thenReturn(List.of(p1, p2));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleResponseDTO result = roleService.updateRole(roleId, request);

        assertNotNull(result);
        assertEquals("SYSTEM_ADMIN", result.getName());
        assertEquals(2, result.getPermissions().size());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testUpdateRole_SystemRole_ThrowsException() {
        role.setSystemRole(true);

        RoleRequestDTO request = new RoleRequestDTO();
        request.setName(fptu.sba301.ats.enums.Role.SYSTEM_ADMIN); // using a valid role enum but it's a system role anyway

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            roleService.updateRole(roleId, request);
        });

        assertEquals("Cannot modify system roles", ex.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testUpdateRole_DuplicateName_ThrowsException() {
        RoleRequestDTO request = new RoleRequestDTO();
        request.setName(fptu.sba301.ats.enums.Role.HR); // different from current "HR_MANAGER"

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.existsByName(fptu.sba301.ats.enums.Role.HR)).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            roleService.updateRole(roleId, request);
        });

        assertEquals("Role name already exists", ex.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    @Test
    void testDeleteRole_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        roleService.deleteRole(roleId);

        verify(roleRepository, times(1)).delete(role);
    }

    @Test
    void testDeleteRole_SystemRole_ThrowsException() {
        role.setSystemRole(true);

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            roleService.deleteRole(roleId);
        });

        assertEquals("Cannot delete system roles", ex.getMessage());
        verify(roleRepository, never()).delete(any(Role.class));
    }

    @Test
    void testDeleteRole_NotFound_ThrowsException() {
        UUID unknownId = UUID.randomUUID();
        when(roleRepository.findById(unknownId)).thenReturn(Optional.empty());

        BusinessException ex = assertThrows(BusinessException.class, () -> {
            roleService.deleteRole(unknownId);
        });

        assertEquals("Role not found", ex.getMessage());
        verify(roleRepository, never()).delete(any(Role.class));
    }
}
