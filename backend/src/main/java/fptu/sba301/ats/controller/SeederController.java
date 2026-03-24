package fptu.sba301.ats.controller;

import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.Role;
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

    @GetMapping("/api/v1/seed-admin")
    public String seed() {
        if (!userRepository.existsByEmailAndDeletedFalse("admin@ats.com")) {
            User admin = new User();
            admin.setEmail("admin@ats.com");
            admin.setPasswordHash(passwordEncoder.encode("password"));
            admin.setFullName("System Admin");
            admin.setRole(Role.HR_MANAGER);
            admin.setActive(true);
            userRepository.save(admin);
            
            User hr = new User();
            hr.setEmail("hr@ats.com");
            hr.setPasswordHash(passwordEncoder.encode("password"));
            hr.setFullName("HR Manager");
            hr.setRole(Role.HR);
            hr.setActive(true);
            userRepository.save(hr);
            
            return "Admin (admin@ats.com/password) and HR (hr@ats.com/password) created!";
        }
        return "Users already exist.";
    }

    @GetMapping("/api/v1/seed-demo-data")
    public String seedDemoData() {
        seed();
        return demoDataSeederService.seedDemoData();
    }
}
