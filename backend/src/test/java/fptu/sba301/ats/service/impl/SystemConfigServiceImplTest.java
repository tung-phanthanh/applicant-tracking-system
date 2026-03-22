package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.entity.SystemConfig;
import fptu.sba301.ats.repository.SystemConfigRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SystemConfigServiceImplTest {

    @Mock
    private SystemConfigRepository systemConfigRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private SystemConfigServiceImpl systemConfigService;

    @Test
    void testGetAllConfigs_Success() {
        SystemConfig c1 = SystemConfig.builder().key("app.name").value("ATS").build();
        SystemConfig c2 = SystemConfig.builder().key("app.version").value("1.0").build();

        when(systemConfigRepository.findAll()).thenReturn(List.of(c1, c2));

        Map<String, String> result = systemConfigService.getAllConfigs();

        assertEquals(2, result.size());
        assertEquals("ATS", result.get("app.name"));
        assertEquals("1.0", result.get("app.version"));
    }

    @Test
    void testUpdateConfigs_NewKeys_CreatesEntries() {
        when(systemConfigRepository.findById("new.key")).thenReturn(Optional.empty());
        when(systemConfigRepository.save(any(SystemConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        systemConfigService.updateConfigs(Map.of("new.key", "new.value"));

        ArgumentCaptor<SystemConfig> captor = ArgumentCaptor.forClass(SystemConfig.class);
        verify(systemConfigRepository, times(1)).save(captor.capture());

        SystemConfig saved = captor.getValue();
        assertEquals("new.key", saved.getKey());
        assertEquals("new.value", saved.getValue());
        assertNotNull(saved.getUpdatedAt());
    }

    @Test
    void testUpdateConfigs_ExistingKeys_UpdatesValues() {
        SystemConfig existing = SystemConfig.builder()
                .key("app.name")
                .value("OldName")
                .updatedAt(Instant.now().minusSeconds(3600))
                .build();

        when(systemConfigRepository.findById("app.name")).thenReturn(Optional.of(existing));
        when(systemConfigRepository.save(any(SystemConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        systemConfigService.updateConfigs(Map.of("app.name", "NewName"));

        verify(systemConfigRepository, times(1)).save(existing);
        assertEquals("NewName", existing.getValue());
    }

    @Test
    void testUpdateConfigs_NullOrEmpty_DoesNothing() {
        systemConfigService.updateConfigs(null);
        systemConfigService.updateConfigs(Collections.emptyMap());

        verify(systemConfigRepository, never()).save(any());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void testUpdateConfigs_FiresSystemEvent() {
        when(systemConfigRepository.findById("key1")).thenReturn(Optional.empty());
        when(systemConfigRepository.save(any(SystemConfig.class))).thenAnswer(inv -> inv.getArgument(0));

        systemConfigService.updateConfigs(Map.of("key1", "val1"));

        verify(eventPublisher, times(1)).publishEvent(any());
    }
}
