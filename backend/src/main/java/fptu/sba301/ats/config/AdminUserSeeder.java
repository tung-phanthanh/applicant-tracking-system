package fptu.sba301.ats.config;

import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.Role;
import fptu.sba301.ats.repository.RoleRepository;
import fptu.sba301.ats.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserSeeder {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    private static final String ADMIN_EMAIL = "admin@ats.com";
    private static final String ADMIN_PASSWORD = "admin123456";
    private static final String HR_EMAIL = "hr@ats.com";
    private static final String HR_PASSWORD = "hr123456";
    
    @Transactional
    public void seedAdminUser() {
        seedUser(ADMIN_EMAIL, ADMIN_PASSWORD, "System Administrator", Role.SYSTEM_ADMIN);
        seedUser(HR_EMAIL, HR_PASSWORD, "HR Specialist", Role.HR);
    }

    private void seedUser(String email, String password, String fullName, Role roleName) {
        if (userRepository.existsByEmailAndDeletedFalse(email)) {
            log.info(" User already exists: {}", email);
            return;
        }
        
        var role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException(roleName + " role not found!"));
        
        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(role)
                .active(true)
                .deleted(false)
                .build();
        
        userRepository.save(user);
        log.info(" User created: {} / password: {}", email, password);
    }
}
