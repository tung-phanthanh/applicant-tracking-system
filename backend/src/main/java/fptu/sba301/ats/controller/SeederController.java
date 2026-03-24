package fptu.sba301.ats.controller;

import fptu.sba301.ats.config.AdminUserSeeder;
import fptu.sba301.ats.service.DemoDataSeederService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class SeederController {
    
    private final AdminUserSeeder adminUserSeeder;
    private final DemoDataSeederService demoDataSeederService;

    @GetMapping("/api/v1/seed-admin")
    public String seedAdmin() {
        log.info(" Seeding admin user...");
        try {
            adminUserSeeder.seedAdminUser();
            return " Admin user seeded successfully! Email: admin@ats.com\n" +
                   " Change password after first login!\n" + 
                   " Remove this endpoint from permitAll() after seeding!";
        } catch (Exception e) {
            log.error(" Error seeding admin user", e);
            return " Error: " + e.getMessage();
        }
    }

    @GetMapping("/api/v1/seed-demo-data")
    public String seedDemoData() {
        log.info(" Seeding demo data...");
        try {
            seedAdmin();  // Ensure admin exists first
            return demoDataSeederService.seedDemoData();
        } catch (Exception e) {
            log.error(" Error seeding demo data", e);
            return " Error: " + e.getMessage();
        }
    }
}
