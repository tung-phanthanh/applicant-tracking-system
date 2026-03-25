package fptu.sba301.ats.config;

import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.Role;
import fptu.sba301.ats.repository.DepartmentRepository;
import fptu.sba301.ats.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

@Configuration
public class DataSeedConfig {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedUsers(UserRepository userRepository,
                                       DepartmentRepository departmentRepository,
                                       PasswordEncoder passwordEncoder) {
        return args -> {
            Department engineering = departmentRepository.findByNameIgnoreCase("Engineering")
                    .orElseThrow(() -> new IllegalStateException("Department Engineering not found"));

            Department hr = departmentRepository.findByNameIgnoreCase("HR")
                    .orElseThrow(() -> new IllegalStateException("Department HR not found"));

            createUser(userRepository, passwordEncoder,
                    UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                    "admin@example.com", "admin123",
                    "Admin User", Role.SYSTEM_ADMIN, engineering);

            createUser(userRepository, passwordEncoder,
                    UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                    "manager@example.com", "manager123",
                    "HR Manager", Role.HR_MANAGER, engineering);

            createUser(userRepository, passwordEncoder,
                    UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
                    "hr@example.com", "hr123",
                    "HR User", Role.HR, hr);
        };
    }

    private void createUser(UserRepository userRepository,
                            PasswordEncoder passwordEncoder,
                            UUID id,
                            String email,
                            String rawPassword,
                            String fullName,
                            Role role,
                            Department department) {

        userRepository.findByEmailAndDeletedFalse(email).ifPresentOrElse(existing -> {
        }, () -> {
            User user = User.builder()
                    .id(id)
                    .email(email)
                    .passwordHash(passwordEncoder.encode(rawPassword))
                    .fullName(fullName)
                    .avatarURL("")
                    .active(true)
                    .deleted(false)
                    .accountLocked(false)
                    .role(role)
                    .department(department)
                    .build();

            userRepository.save(user);
        });
    }
}