package fptu.sba301.ats.config;

import fptu.sba301.ats.entity.Role;
import fptu.sba301.ats.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static fptu.sba301.ats.enums.Role.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final RoleRepository roleRepository;
    private final AdminUserSeeder adminUserSeeder;

    @Override
    public void run(String... args) throws Exception {
        log.info(" Starting data initialization...");
        
        try {
            // 1️⃣ Seed all roles
            seedRoles();
            
            // 2️⃣ Create admin users
            adminUserSeeder.seedAdminUser();
            
            log.info(" Data initialization completed successfully!");
        } catch (Exception e) {
            log.error(" Error during data initialization", e);
            throw e;
        }
    }
    
    private void seedRoles() {
        log.info(" Seeding roles...");
        
        fptu.sba301.ats.enums.Role[] roles = fptu.sba301.ats.enums.Role.values();
        for (fptu.sba301.ats.enums.Role roleEnum : roles) {
            if (!roleRepository.existsByName(roleEnum)) {
                Role role = Role.builder()
                        .name(roleEnum)
                        .description(getDescription(roleEnum))
                        .build();
                roleRepository.save(role);
                log.info(" Role created: {}", roleEnum);
            }
        }
        
        log.info(" Roles seeding completed!");
    }
    
    private String getDescription(fptu.sba301.ats.enums.Role role) {
        return switch (role) {
            case SYSTEM_ADMIN -> "System Administrator - full access";
            case HR -> "HR Specialist - can manage candidates and jobs";
            case HR_MANAGER -> "HR Manager - can manage team and reports";
            case INTERVIEWER -> "Interviewer - can conduct interviews";
        };
    }
}
