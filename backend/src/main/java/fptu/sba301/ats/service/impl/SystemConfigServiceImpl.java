package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.annotation.LogAudit;
import fptu.sba301.ats.event.SystemEvent;
import fptu.sba301.ats.entity.SystemConfig;
import fptu.sba301.ats.repository.SystemConfigRepository;
import fptu.sba301.ats.service.SystemConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SystemConfigServiceImpl implements SystemConfigService {

    private final SystemConfigRepository systemConfigRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public Map<String, String> getAllConfigs() {
        return systemConfigRepository.findAll().stream()
                .collect(Collectors.toMap(SystemConfig::getKey, SystemConfig::getValue));
    }

    @Override
    @Transactional
    @LogAudit(action = "UPDATE", resource = "SystemConfig")
    public void updateConfigs(Map<String, String> configs) {
        if (configs == null || configs.isEmpty()) {
            return;
        }

        configs.forEach((key, value) -> {
            SystemConfig config = systemConfigRepository.findById(key)
                    .orElseGet(() -> {
                        SystemConfig newConfig = new SystemConfig();
                        newConfig.setKey(key);
                        return newConfig;
                    });
            config.setValue(value);
            config.setUpdatedAt(Instant.now());
            systemConfigRepository.save(config);
        });

        // Fire a system event for updating the config
        eventPublisher.publishEvent(new SystemEvent(this,
                "System Configuration Updated",
                "Global system settings have been modified.",
                "system",
                "/system-config"));
    }
}
