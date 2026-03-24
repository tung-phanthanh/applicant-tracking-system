package fptu.sba301.ats.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {
    
    private final AdminPermissionRegistry permissionRegistry;
    private final AdminUserSeeder adminUserSeeder;

    @Override
    public void run(String... args) throws Exception {
        log.info(" Starting data initialization...");
        
        try {
            // 1️ Seed all permissions (tự động assign tới roles)
            permissionRegistry.seedAllPermissions();
            
            // 2️ Create admin user
            adminUserSeeder.seedAdminUser();
            
            log.info(" Data initialization completed successfully!");
        } catch (Exception e) {
            log.error(" Error during data initialization", e);
            throw e;
        }
    }
}
