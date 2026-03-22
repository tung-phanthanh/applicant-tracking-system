package fptu.sba301.ats.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fptu.sba301.ats.dto.response.AuditLogResponseDTO;
import fptu.sba301.ats.entity.AuditLog;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.repository.AuditLogRepository;
import fptu.sba301.ats.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuditLogServiceImplTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private UserRepository userRepository;

    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private AuditLogServiceImpl auditLogService;

    private UUID userId;
    private User user;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = User.builder()
                .id(userId)
                .email("admin@ats.com")
                .fullName("Admin User")
                .build();

        auditLog = AuditLog.builder()
                .id(1L)
                .userId(userId)
                .action("CREATE")
                .entityType("Role")
                .entityId("role-123")
                .oldValue(null)
                .newValue("{\"name\":\"HR_MANAGER\"}")
                .ipAddress("127.0.0.1")
                .userAgent("Mozilla/5.0")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void testGetAllLogs_ReturnsSortedList() {
        when(auditLogRepository.findAll(any(Sort.class))).thenReturn(List.of(auditLog));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<AuditLogResponseDTO> result = auditLogService.getAllLogs();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("CREATE", result.get(0).getAction());
        assertEquals("Admin User", result.get(0).getUserFullName());
        verify(auditLogRepository).findAll(any(Sort.class));
    }

    @Test
    void testGetLogsByAction_ReturnsFilteredList() {
        when(auditLogRepository.findByAction("CREATE")).thenReturn(List.of(auditLog));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<AuditLogResponseDTO> result = auditLogService.getLogsByAction("CREATE");

        assertEquals(1, result.size());
        assertEquals("CREATE", result.get(0).getAction());
        assertEquals("Role", result.get(0).getEntityType());
    }

    @Test
    void testLogAction_WithUserId_SavesLog() {
        Map<String, Object> newValue = Map.of("name", "HR_MANAGER");

        auditLogService.logAction(userId, "CREATE", "Role", "role-123",
                null, newValue, "127.0.0.1", "Mozilla/5.0");

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testLogAction_WithoutUserId_SavesLog() {
        auditLogService.logAction(null, "SYSTEM_START", "System", null,
                null, null, null, null);

        verify(auditLogRepository, times(1)).save(any(AuditLog.class));
    }

    @Test
    void testMapToDTO_WithUser_IncludesUserInfo() {
        when(auditLogRepository.findByAction("CREATE")).thenReturn(List.of(auditLog));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        List<AuditLogResponseDTO> result = auditLogService.getLogsByAction("CREATE");

        AuditLogResponseDTO dto = result.get(0);
        assertEquals(userId, dto.getUserId());
        assertEquals("admin@ats.com", dto.getUserEmail());
        assertEquals("Admin User", dto.getUserFullName());
    }

    @Test
    void testMapToDTO_WithoutUser_NoUserInfo() {
        AuditLog logNoUser = AuditLog.builder()
                .id(2L)
                .userId(null)
                .action("SYSTEM")
                .entityType("System")
                .entityId(null)
                .createdAt(Instant.now())
                .build();

        when(auditLogRepository.findByAction("SYSTEM")).thenReturn(List.of(logNoUser));

        List<AuditLogResponseDTO> result = auditLogService.getLogsByAction("SYSTEM");

        AuditLogResponseDTO dto = result.get(0);
        assertNull(dto.getUserId());
        assertNull(dto.getUserEmail());
        assertNull(dto.getUserFullName());
    }
}
