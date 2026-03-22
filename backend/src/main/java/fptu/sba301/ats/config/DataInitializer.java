package fptu.sba301.ats.config;

import fptu.sba301.ats.entity.Permission;
import fptu.sba301.ats.entity.Role;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.repository.PermissionRepository;
import fptu.sba301.ats.repository.RoleRepository;
import fptu.sba301.ats.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    public void run(String... args) throws Exception {
        // 1. Seed Permissions - Core Admin/Auth
        seedPermission("LOGIN_ACCESS", "System", "Basic login access");
        seedPermission("PROFILE_MANAGE", "System", "Manage own profile and change password");
        seedPermission("USER_MANAGE", "System", "Manage users");
        seedPermission("WORKFLOW_MANAGE", "System", "Manage workflows");

        // Seed Permissions - Job Module
        seedPermission("JOB_VIEW", "Jobs", "View job list and details");
        seedPermission("JOB_CREATE", "Jobs", "Create new jobs");
        seedPermission("JOB_EDIT", "Jobs", "Edit existing jobs");
        seedPermission("JOB_APPROVE", "Jobs", "Approve jobs");

        // Seed Permissions - Candidate Pipeline
        seedPermission("CANDIDATE_VIEW", "Candidates", "View pipeline, lists, details, history");
        seedPermission("CANDIDATE_MANAGE", "Candidates", "Add candidates and upload CVs");
        seedPermission("CV_VIEW", "Candidates", "View CV files");

        // Seed Permissions - Interview Module
        seedPermission("INTERVIEW_VIEW", "Interviews", "View interview calendar and details");
        seedPermission("INTERVIEW_SCHEDULE", "Interviews", "Schedule new interviews");
        seedPermission("INTERVIEW_FEEDBACK", "Interviews", "Submit interview feedback");

        // Seed Permissions - Scorecards & Evaluation
        seedPermission("SCORECARD_MANAGE", "Evaluation", "Manage scorecard templates");
        seedPermission("EVALUATION_SUMMARY_VIEW", "Evaluation", "View evaluation summary and rankings");

        // Seed Permissions - Offer & Onboarding
        seedPermission("OFFER_VIEW", "Offers", "View offers");
        seedPermission("OFFER_DRAFT", "Offers", "Draft offers");
        seedPermission("OFFER_APPROVE", "Offers", "Approve offers");
        seedPermission("OFFER_PDF_PREVIEW", "Offers", "Preview offer PDF");
        seedPermission("ONBOARDING_MANAGE", "Onboarding", "Manage onboarding checklists");

        // 2. Seed Roles
        Role adminRole = seedRole(fptu.sba301.ats.enums.Role.SYSTEM_ADMIN, "Full system access", 
                java.util.List.of(
                    "LOGIN_ACCESS", "PROFILE_MANAGE", "USER_MANAGE", "WORKFLOW_MANAGE",
                    "JOB_VIEW", "JOB_CREATE", "JOB_EDIT", "JOB_APPROVE",
                    "CANDIDATE_VIEW", "CANDIDATE_MANAGE", "CV_VIEW",
                    "INTERVIEW_VIEW", "INTERVIEW_SCHEDULE", "INTERVIEW_FEEDBACK",
                    "SCORECARD_MANAGE", "EVALUATION_SUMMARY_VIEW",
                    "OFFER_VIEW", "OFFER_DRAFT", "OFFER_APPROVE", "OFFER_PDF_PREVIEW", "ONBOARDING_MANAGE"
                ));
                
        seedRole(fptu.sba301.ats.enums.Role.HR_MANAGER, "Human Resources Manager", 
                java.util.List.of(
                    "LOGIN_ACCESS", "PROFILE_MANAGE",
                    "JOB_VIEW", "JOB_CREATE", "JOB_EDIT", "JOB_APPROVE",
                    "CANDIDATE_VIEW", "CANDIDATE_MANAGE", "CV_VIEW",
                    "INTERVIEW_VIEW", "INTERVIEW_SCHEDULE", "INTERVIEW_FEEDBACK",
                    "SCORECARD_MANAGE", "EVALUATION_SUMMARY_VIEW",
                    "OFFER_VIEW", "OFFER_DRAFT", "OFFER_APPROVE", "OFFER_PDF_PREVIEW", "ONBOARDING_MANAGE"
                ));
                
        seedRole(fptu.sba301.ats.enums.Role.HR, "HR Specialist", 
                java.util.List.of(
                    "LOGIN_ACCESS", "PROFILE_MANAGE",
                    "JOB_VIEW", "JOB_CREATE", "JOB_EDIT",
                    "CANDIDATE_VIEW", "CANDIDATE_MANAGE", "CV_VIEW",
                    "INTERVIEW_VIEW", "INTERVIEW_SCHEDULE",
                    "EVALUATION_SUMMARY_VIEW",
                    "OFFER_VIEW", "OFFER_DRAFT", "ONBOARDING_MANAGE"
                ));
                
        seedRole(fptu.sba301.ats.enums.Role.INTERVIEWER, "Interviewer", 
                java.util.List.of(
                    "LOGIN_ACCESS", "PROFILE_MANAGE",
                    "JOB_VIEW",
                    "CANDIDATE_VIEW", "CV_VIEW",
                    "INTERVIEW_VIEW", "INTERVIEW_FEEDBACK"
                ));

        // 3. Seed Admin User
        if (userRepository.findByEmail("admin@ats.com").isEmpty()) {
            User admin = User.builder()
                    .email("admin@ats.com")
                    .passwordHash(passwordEncoder.encode("admin123"))
                    .fullName("System Admin")
                    .active(true)
                    .role(adminRole)
                    .build();
            userRepository.save(admin);
            System.out.println("Default admin user created: admin@ats.com / admin123");
        }
    }

    private void seedPermission(String key, String category, String label) {
        if (!permissionRepository.existsById(key)) {
            Permission permission = new Permission();
            permission.setKey(key);
            permission.setCategory(category);
            permission.setLabel(label);
            permissionRepository.save(permission);
        }
    }

    private Role seedRole(fptu.sba301.ats.enums.Role name, String description, java.util.List<String> permissionKeys) {
        Role role = roleRepository.findByName(name).orElseGet(() -> {
            Role newRole = new Role();
            newRole.setName(name);
            newRole.setDescription(description);
            return newRole;
        });

        java.util.Set<Permission> permissions = new java.util.HashSet<>();
        for (String key : permissionKeys) {
            permissionRepository.findById(key).ifPresent(permissions::add);
        }
        role.setPermissions(permissions);
        return roleRepository.save(role);
    }
}
