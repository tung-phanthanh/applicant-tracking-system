package fptu.sba301.ats.service;

import fptu.sba301.ats.dto.request.RoleRequestDTO;
import fptu.sba301.ats.dto.response.RoleResponseDTO;
import fptu.sba301.ats.entity.Permission;
import fptu.sba301.ats.entity.Role;
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
        role.setName("HR_MANAGER");
        role.setDescription("Human Resources Manager");
        role.setSystemRole(false);

        Permission p1 = new Permission("VIEW_CANDIDATE", "View Candidates", "Recruitment");
        role.setPermissions(new HashSet<>(Set.of(p1)));
    }

    @Test
    void testUpdateRole_Success() {
        RoleRequestDTO request = new RoleRequestDTO();
        request.setName("HR_DIRECTOR");
        request.setDescription("HR Director");
        request.setPermissionKeys(List.of("VIEW_CANDIDATE", "EDIT_CANDIDATE"));

        Permission p1 = new Permission("VIEW_CANDIDATE", "View Candidates", "Recruitment");
        Permission p2 = new Permission("EDIT_CANDIDATE", "Edit Candidates", "Recruitment");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(permissionRepository.findAllById(request.getPermissionKeys())).thenReturn(List.of(p1, p2));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleResponseDTO result = roleService.updateRole(roleId, request);

        assertNotNull(result);
        assertEquals("HR_DIRECTOR", result.getName());
        assertEquals(2, result.getPermissions().size());
        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void testUpdateRole_SystemRoleThrowsException() {
        role.setSystemRole(true);

        RoleRequestDTO request = new RoleRequestDTO();
        request.setName("SYSTEM_ADMIN_NEW");

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            roleService.updateRole(roleId, request);
        });

        assertEquals("Cannot modify system roles", exception.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testDeleteRole_Success() {
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));

        roleService.deleteRole(roleId);

        verify(roleRepository, times(1)).delete(role);
    }
}
