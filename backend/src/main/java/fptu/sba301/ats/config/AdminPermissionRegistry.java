package fptu.sba301.ats.config;

import fptu.sba301.ats.constant.PermissionConstants;
import fptu.sba301.ats.entity.Permission;
import fptu.sba301.ats.entity.Role;
import fptu.sba301.ats.repository.PermissionRepository;
import fptu.sba301.ats.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import static fptu.sba301.ats.enums.Role.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class AdminPermissionRegistry {
    
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    
    @Transactional
    public void seedAllPermissions() {
        log.info(" Starting permission seeding...");
        
        // 1. Get or create all permissions
        Map<String, Permission> permissions = new HashMap<>();
        for (PermissionDef def : getPermissionDefinitions()) {
            Permission perm = permissionRepository.findById(def.key)
                    .orElseGet(() -> {
                        var newPerm = Permission.builder()
                                .key(def.key)
                                .label(def.label)
                                .category(def.category)
                                .build();
                        permissionRepository.save(newPerm);
                        log.debug(" Permission created: {} ({})", def.key, def.label);
                        return newPerm;
                    });
            permissions.put(def.key, perm);
        }
        
        // 2. Assign permissions to SYSTEM_ADMIN role
        assignPermissionsToRole(fptu.sba301.ats.enums.Role.SYSTEM_ADMIN, permissions.values());
        
        // 3. Assign minimal permissions to other roles
        assignPermissionsToRole(fptu.sba301.ats.enums.Role.HR, getHRPermissions(permissions));
        assignPermissionsToRole(fptu.sba301.ats.enums.Role.HR_MANAGER, getHRManagerPermissions(permissions));
        assignPermissionsToRole(fptu.sba301.ats.enums.Role.INTERVIEWER, getInterviewerPermissions(permissions));
        
        log.info(" Permission seeding completed!");
    }
    
    /**
     * Define all system permissions. Auto-seeded on startup.
     * Add new permission: (1) constant in PermissionConstants, (2) PermissionDef here
     */
    private List<PermissionDef> getPermissionDefinitions() {
        return Arrays.asList(
                // ===== ADMIN PANEL (SYSTEM_ADMIN only) =====
                new PermissionDef(
                        PermissionConstants.DASHBOARD_VIEW,
                        "View Admin Dashboard",
                        "Admin Panel"
                ),
                new PermissionDef(
                        PermissionConstants.ROLE_MANAGE,
                        "Manage Roles & Permissions",
                        "Admin Panel"
                ),
                new PermissionDef(
                        PermissionConstants.DEPARTMENT_MANAGE,
                        "Manage Departments",
                        "Admin Panel"
                ),
                new PermissionDef(
                        PermissionConstants.SYSTEM_CONFIG_MANAGE,
                        "Manage System Configuration",
                        "Admin Panel"
                ),
                new PermissionDef(
                        PermissionConstants.AUDIT_LOG_VIEW,
                        "View Audit Logs",
                        "Admin Panel"
                ),
                new PermissionDef(
                        PermissionConstants.NOTIFICATION_MANAGE,
                        "Manage Notifications",
                        "Admin Panel"
                ),
                
                // ===== USER MANAGEMENT =====
                new PermissionDef(
                        PermissionConstants.USER_CREATE,
                        "Create Users",
                        "User Management"
                ),
                new PermissionDef(
                        PermissionConstants.USER_VIEW,
                        "View Users",
                        "User Management"
                ),
                new PermissionDef(
                        PermissionConstants.USER_UPDATE,
                        "Update Users",
                        "User Management"
                ),
                new PermissionDef(
                        PermissionConstants.USER_DELETE,
                        "Delete Users",
                        "User Management"
                ),
                
                // ===== JOB MANAGEMENT =====
                new PermissionDef(
                        PermissionConstants.JOB_CREATE,
                        "Create Job Postings",
                        "Job Management"
                ),
                new PermissionDef(
                        PermissionConstants.JOB_VIEW,
                        "View Job Postings",
                        "Job Management"
                ),
                new PermissionDef(
                        PermissionConstants.JOB_EDIT,
                        "Edit Job Postings",
                        "Job Management"
                ),
                new PermissionDef(
                        PermissionConstants.JOB_APPROVE,
                        "Approve Job Postings",
                        "Job Management"
                ),
                new PermissionDef(
                        PermissionConstants.JOB_DELETE,
                        "Delete Job Postings",
                        "Job Management"
                ),
                
                // ===== CANDIDATE MANAGEMENT =====
                new PermissionDef(
                        PermissionConstants.CANDIDATE_VIEW,
                        "View Candidates",
                        "Candidate Management"
                ),
                new PermissionDef(
                        PermissionConstants.CANDIDATE_MANAGE,
                        "Manage Candidates",
                        "Candidate Management"
                ),
                
                // ===== INTERVIEW MANAGEMENT =====
                new PermissionDef(
                        PermissionConstants.INTERVIEW_SCHEDULE,
                        "Schedule Interviews",
                        "Interview Management"
                ),
                new PermissionDef(
                        PermissionConstants.INTERVIEW_CONDUCT,
                        "Conduct Interviews",
                        "Interview Management"
                ),
                new PermissionDef(
                        PermissionConstants.INTERVIEW_EVALUATE,
                        "Evaluate Interviews",
                        "Interview Management"
                ),
                
                // ===== OFFER MANAGEMENT =====
                new PermissionDef(
                        PermissionConstants.OFFER_CREATE,
                        "Create Offers",
                        "Offer Management"
                ),
                new PermissionDef(
                        PermissionConstants.OFFER_SEND,
                        "Send Offers",
                        "Offer Management"
                ),
                new PermissionDef(
                        PermissionConstants.OFFER_APPROVE,
                        "Approve Offers",
                        "Offer Management"
                )
        );
    }
    
    private List<Permission> getHRPermissions(Map<String, Permission> permissions) {
        return Arrays.asList(
                permissions.get(PermissionConstants.JOB_CREATE),
                permissions.get(PermissionConstants.JOB_VIEW),
                permissions.get(PermissionConstants.JOB_EDIT),
                permissions.get(PermissionConstants.CANDIDATE_VIEW),
                permissions.get(PermissionConstants.CANDIDATE_MANAGE),
                permissions.get(PermissionConstants.INTERVIEW_SCHEDULE),
                permissions.get(PermissionConstants.OFFER_CREATE),
                permissions.get(PermissionConstants.OFFER_SEND)
        );
    }
    
    private List<Permission> getHRManagerPermissions(Map<String, Permission> permissions) {
        return Arrays.asList(
                permissions.get(PermissionConstants.JOB_VIEW),
                permissions.get(PermissionConstants.CANDIDATE_VIEW),
                permissions.get(PermissionConstants.INTERVIEW_CONDUCT),
                permissions.get(PermissionConstants.INTERVIEW_EVALUATE),
                permissions.get(PermissionConstants.OFFER_APPROVE)
        );
    }
    
    private List<Permission> getInterviewerPermissions(Map<String, Permission> permissions) {
        return Arrays.asList(
                permissions.get(PermissionConstants.CANDIDATE_VIEW),
                permissions.get(PermissionConstants.INTERVIEW_CONDUCT),
                permissions.get(PermissionConstants.INTERVIEW_EVALUATE)
        );
    }
    
    private void assignPermissionsToRole(fptu.sba301.ats.enums.Role roleEnum, Collection<Permission> perms) {
        Role role = roleRepository.findByName(roleEnum)
                .orElseGet(() -> {
                    var newRole = Role.builder()
                            .name(roleEnum)
                            .description("Auto-created role for " + roleEnum)
                            .systemRole(true)
                            .permissions(new HashSet<>())
                            .build();
                    return roleRepository.save(newRole);
                });
        
        Set<Permission> currentPerms = role.getPermissions() != null 
                ? role.getPermissions() 
                : new HashSet<>();
        
        currentPerms.addAll(perms);
        role.setPermissions(currentPerms);
        roleRepository.save(role);
        
        log.debug(" Assigned {} permissions to {}", perms.size(), roleEnum);
    }

    private static class PermissionDef {
        String key;
        String label;
        String category;
        
        PermissionDef(String key, String label, String category) {
            this.key = key;
            this.label = label;
            this.category = category;
        }
    }
}
