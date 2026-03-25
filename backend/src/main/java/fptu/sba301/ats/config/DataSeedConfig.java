package fptu.sba301.ats.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeedConfig {

    @Bean
    public CommandLineRunner seedUsers(DemoUserSeedService demoUserSeedService) {
        return args -> demoUserSeedService.seedDemoUsersIfAbsent();
    }
}
