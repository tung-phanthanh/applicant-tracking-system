package fptu.sba301.ats.constant;


public class PermissionConstants {
    
    // ============= ADMIN PANEL PERMISSIONS =============
    
    // Dashboard
    public static final String DASHBOARD_VIEW = "DASHBOARD_VIEW";
    
    // Role & Permission Management
    public static final String ROLE_MANAGE = "ROLE_MANAGE";
    
    // Department Management
    public static final String DEPARTMENT_MANAGE = "DEPARTMENT_MANAGE";
    
    // System Configuration
    public static final String SYSTEM_CONFIG_MANAGE = "SYSTEM_CONFIG_MANAGE";
    
    // Audit Log
    public static final String AUDIT_LOG_VIEW = "AUDIT_LOG_VIEW";
    
    // Notification (Admin - bulk send)
    public static final String NOTIFICATION_MANAGE = "NOTIFICATION_MANAGE";
    
    
    // ============= USER MANAGEMENT PERMISSIONS =============
    
    // User CRUD
    public static final String USER_CREATE = "USER_CREATE";
    public static final String USER_VIEW = "USER_VIEW";
    public static final String USER_UPDATE = "USER_UPDATE";
    public static final String USER_DELETE = "USER_DELETE";
    
    
    // ============= JOB MANAGEMENT PERMISSIONS =============
    
    // Job CRUD
    public static final String JOB_CREATE = "JOB_CREATE";
    public static final String JOB_VIEW = "JOB_VIEW";
    public static final String JOB_EDIT = "JOB_EDIT";
    public static final String JOB_APPROVE = "JOB_APPROVE";
    public static final String JOB_DELETE = "JOB_DELETE";
    
    
    // ============= CANDIDATE MANAGEMENT PERMISSIONS =============
    
    public static final String CANDIDATE_VIEW = "CANDIDATE_VIEW";
    public static final String CANDIDATE_MANAGE = "CANDIDATE_MANAGE";
    
    
    // ============= INTERVIEW MANAGEMENT PERMISSIONS =============
    
    public static final String INTERVIEW_SCHEDULE = "INTERVIEW_SCHEDULE";
    public static final String INTERVIEW_CONDUCT = "INTERVIEW_CONDUCT";
    public static final String INTERVIEW_EVALUATE = "INTERVIEW_EVALUATE";
    
    
    // ============= OFFER MANAGEMENT PERMISSIONS =============
    
    public static final String OFFER_CREATE = "OFFER_CREATE";
    public static final String OFFER_SEND = "OFFER_SEND";
    public static final String OFFER_APPROVE = "OFFER_APPROVE";
    
    
    // ============= UTILITY =============
    
    /**
     * Kiểm tra xem permission key có hợp lệ không
     * (Dùng khi validate hoặc debug)
     */
    public static boolean isValidPermissionKey(String key) {
        try {
            java.lang.reflect.Field[] fields = PermissionConstants.class.getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if (field.get(null).equals(key)) {
                    return true;
                }
            }
        } catch (IllegalAccessException e) {
            // Ignore
        }
        return false;
    }
}
