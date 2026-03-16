package fptu.sba301.ats.config;

import fptu.sba301.ats.entity.Department;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.enums.Role;
import fptu.sba301.ats.enums.UserStatus;
import fptu.sba301.ats.repository.DepartmentRepository;
import fptu.sba301.ats.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@Profile({"h2", "mysql"})
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        if (departmentRepository.count() == 0 && userRepository.count() == 0) {
            initDepartments();
            initUsers();
            log.info("Data initialization completed");
        } else {
            log.info("Data already exists, skipping initialization");
        }
    }

    private void initDepartments() {
        if (departmentRepository.count() == 0) {
            Department eng = new Department();
            eng.setId(UUID.fromString("00000000-0000-0000-0000-000000000001"));
            eng.setName("Engineering");
            eng.setDescription("Software Engineering department");
            departmentRepository.save(eng);

            Department product = new Department();
            product.setId(UUID.fromString("00000000-0000-0000-0000-000000000002"));
            product.setName("Product");
            product.setDescription("Product & Design department");
            departmentRepository.save(product);

            Department hr = new Department();
            hr.setId(UUID.fromString("00000000-0000-0000-0000-000000000003"));
            hr.setName("Human Resources");
            hr.setDescription("People & Culture department");
            departmentRepository.save(hr);

            log.info("Departments initialized");
        }
    }

    private void initUsers() {
        if (userRepository.count() == 0) {
            Optional<Department> hrDept = departmentRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000003"));
            Optional<Department> engDept = departmentRepository.findById(UUID.fromString("00000000-0000-0000-0000-000000000001"));

            String password = "12345678";

            User admin = new User();
            admin.setId(UUID.fromString("10000000-0000-0000-0000-000000000001"));
            admin.setEmail("admin@ats.local");
            admin.setPasswordHash(passwordEncoder.encode(password));
            admin.setFullName("System Admin");
            admin.setRole(Role.SYSTEM_ADMIN);
            admin.setStatus(UserStatus.ACTIVE);
            admin.setActive(true);
            admin.setDeleted(false);
            admin.setAccountLocked(false);
            userRepository.save(admin);

            User hr = new User();
            hr.setId(UUID.fromString("10000000-0000-0000-0000-000000000002"));
            hr.setEmail("hr@ats.local");
            hr.setPasswordHash(passwordEncoder.encode(password));
            hr.setFullName("Nguyen HR");
            hr.setRole(Role.HR);
            hr.setStatus(UserStatus.ACTIVE);
            hr.setActive(true);
            hr.setDeleted(false);
            hr.setAccountLocked(false);
            hr.setDepartment(hrDept.orElse(null));
            userRepository.save(hr);

            User hrm = new User();
            hrm.setId(UUID.fromString("10000000-0000-0000-0000-000000000003"));
            hrm.setEmail("hrm@ats.local");
            hrm.setPasswordHash(passwordEncoder.encode(password));
            hrm.setFullName("Tran HR Manager");
            hrm.setRole(Role.HR_MANAGER);
            hrm.setStatus(UserStatus.ACTIVE);
            hrm.setActive(true);
            hrm.setDeleted(false);
            hrm.setAccountLocked(false);
            hrm.setDepartment(hrDept.orElse(null));
            userRepository.save(hrm);

            User interviewer = new User();
            interviewer.setId(UUID.fromString("10000000-0000-0000-0000-000000000004"));
            interviewer.setEmail("interviewer@ats.local");
            interviewer.setPasswordHash(passwordEncoder.encode(password));
            interviewer.setFullName("Le Interviewer");
            interviewer.setRole(Role.INTERVIEWER);
            interviewer.setStatus(UserStatus.ACTIVE);
            interviewer.setActive(true);
            interviewer.setDeleted(false);
            interviewer.setAccountLocked(false);
            interviewer.setDepartment(engDept.orElse(null));
            userRepository.save(interviewer);

            log.info("Users initialized with password: {}", password);
        }
    }
}
