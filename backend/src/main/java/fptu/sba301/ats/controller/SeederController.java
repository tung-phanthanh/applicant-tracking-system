package fptu.sba301.ats.controller;

import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.repository.UserRepository;
import fptu.sba301.ats.service.DemoDataSeederService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SeederController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final DemoDataSeederService demoDataSeederService;
    private final fptu.sba301.ats.repository.SystemConfigRepository systemConfigRepository;

    @GetMapping("/api/v1/seed-admin")
    public String seed() {
        if (!userRepository.existsByEmailAndDeletedFalse("admin@ats.com")) {
            User admin = new User();
            admin.setId(java.util.UUID.randomUUID());
            admin.setEmail("admin@ats.com");
            admin.setPasswordHash(passwordEncoder.encode("password"));
            admin.setFullName("System Admin");
            admin.setRole(fptu.sba301.ats.enums.Role.SYSTEM_ADMIN);
            admin.setActive(true);
            userRepository.save(admin);

            User hr = new User();
            hr.setId(java.util.UUID.randomUUID());
            hr.setEmail("hr@ats.com");
            hr.setPasswordHash(passwordEncoder.encode("password"));
            hr.setFullName("HR Manager");
            hr.setRole(fptu.sba301.ats.enums.Role.HR_MANAGER);
            hr.setActive(true);
            userRepository.save(hr);

            return "Admin (admin@ats.com/password) and HR (hr@ats.com/password) created!";
        }
        return "Users already exist.";
    }

    @GetMapping("/api/v1/seed-demo-data")
    public String seedDemoData() {
        seed();
        seedConfigs();
        return demoDataSeederService.seedDemoData();
    }

    private void seedConfigs() {
        if (systemConfigRepository.count() == 0) {
            saveConfig("APP_NAME", "Enterprise Applicant Tracking System");
            saveConfig("CONTACT_EMAIL", "support@ats.com");
            saveConfig("MAX_LOGIN_ATTEMPTS", "5");
            saveConfig("SESSION_TIMEOUT", "3600");
            saveConfig("THEME_COLOR", "#3b82f6");
        }
    }

    private void saveConfig(String key, String value) {
        fptu.sba301.ats.entity.SystemConfig config = new fptu.sba301.ats.entity.SystemConfig();
        config.setKey(key);
        config.setValue(value);
        config.setUpdatedAt(java.time.Instant.now());
        systemConfigRepository.save(config);
    }
}
