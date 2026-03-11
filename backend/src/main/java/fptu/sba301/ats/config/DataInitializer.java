package fptu.sba301.ats.config;

import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.Role;
import fptu.sba301.ats.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("admin@ats.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@ats.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .fullName("System Admin")
                    .active(true)
                    .role(Role.SYSTEM_ADMIN)
                    .build();
            userRepository.save(admin);
            System.out.println("Default admin user created: admin@ats.com / admin123");
        }
    }
}
