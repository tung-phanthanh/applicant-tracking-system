package fptu.sba301.ats.config;

import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.Role;
import fptu.sba301.ats.repository.DepartmentRepository;
import fptu.sba301.ats.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DemoUserSeedService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void seedDemoUsersIfAbsent() {
        Department engineering = departmentRepository.findByNameIgnoreCase("Engineering")
                .orElseThrow(() -> new IllegalStateException("Department Engineering not found"));

        Department hr = departmentRepository.findByNameIgnoreCase("HR")
                .orElseThrow(() -> new IllegalStateException("Department HR not found"));

        createUserIfAbsent(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "admin@example.com", "admin123",
                "Admin User", Role.SYSTEM_ADMIN, engineering);

        createUserIfAbsent(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                "manager@example.com", "manager123",
                "HR Manager", Role.HR_MANAGER, engineering);

        createUserIfAbsent(
                UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
                "hr@example.com", "hr12345",
                "HR User", Role.HR, hr);
    }

    private void createUserIfAbsent(
            UUID id,
            String email,
            String rawPassword,
            String fullName,
            Role role,
            Department department
    ) {
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
