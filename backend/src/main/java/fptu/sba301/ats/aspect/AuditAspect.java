package fptu.sba301.ats.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import fptu.sba301.ats.annotation.LogAudit;
import fptu.sba301.ats.entity.AuditLog;
import fptu.sba301.ats.entity.User;
import fptu.sba301.ats.repository.AuditLogRepository;
import fptu.sba301.ats.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @AfterReturning(pointcut = "@annotation(logAudit)", returning = "result")
    public void logAuditActivity(JoinPoint joinPoint, LogAudit logAudit, Object result) {
        try {
            // --- Get IP & User-Agent ---
            String ipAddress = "Unknown";
            String userAgent = "Unknown";
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = request.getRemoteAddr();
                userAgent = request.getHeader("User-Agent");
            }

            // --- Get authenticated user ---
            String username = "System";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
                username = userDetails.getUsername();
            } else if (authentication != null && authentication.getPrincipal() instanceof String principal) {
                username = principal;
            }
            User user = userRepository.findByEmailAndDeletedFalse(username).orElse(null);

            // --- Resolve resource name ---
            String resource = logAudit.resource();
            if (resource.isEmpty()) {
                resource = joinPoint.getTarget().getClass().getSimpleName();
                if (resource.endsWith("ServiceImpl")) {
                    resource = resource.substring(0, resource.length() - 11);
                }
            }

            // --- Extract entity name/ID from result for description ---
            String entityName = extractEntityName(result);
            String entityId = extractEntityId(result);

            // --- Build a human-readable description ---
            String descriptionText = buildDescription(logAudit.action(), resource, entityName);

            // --- Serialize result to JSON for newValue ---
            String newValueJson = serializeToJson(result, descriptionText);

            // --- Save audit log ---
            AuditLog auditLog = AuditLog.builder()
                    .userId(user != null ? user.getId() : null)
                    .action(logAudit.action())
                    .entityType(resource)
                    .entityId(entityId)
                    .newValue(newValueJson)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent != null ? userAgent : "Unknown")
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Audit log: {} by {}", descriptionText, username);

        } catch (Exception e) {
            log.error("Failed to save audit log for method: {}", joinPoint.getSignature().toShortString(), e);
        }
    }

    /** Try to extract a "name" field from the result DTO via reflection. */
    private String extractEntityName(Object result) {
        if (result == null) return null;
        try {
            Method nameMethod = result.getClass().getMethod("getName");
            Object val = nameMethod.invoke(result);
            return val != null ? val.toString() : null;
        } catch (Exception ignored) {
            return null;
        }
    }

    /** Try to extract the "id" field (UUID) from the result DTO via reflection. */
    private String extractEntityId(Object result) {
        if (result == null) return null;
        try {
            Method idMethod = result.getClass().getMethod("getId");
            Object val = idMethod.invoke(result);
            if (val != null) return val.toString();
        } catch (Exception ignored) {
        }
        return null;
    }

    /**
     * Build a clear human-readable description string.
     * Example: "Created Role: Recruiter", "Deleted Department: Engineering"
     */
    private String buildDescription(String action, String resource, String entityName) {
        String verb = switch (action.toUpperCase()) {
            case "CREATE" -> "Created";
            case "UPDATE" -> "Updated";
            case "DELETE" -> "Deleted";
            case "TOGGLE_STATUS" -> "Toggled status of";
            case "LOGIN" -> "Logged in to";
            case "LOGOUT" -> "Logged out of";
            default -> action;
        };
        if (entityName != null && !entityName.isBlank()) {
            return verb + " " + resource + ": " + entityName;
        }
        return verb + " " + resource;
    }

    /** Serialize result to a JSON string with a "description" field at the top. */
    private String serializeToJson(Object result, String description) {
        try {
            Map<String, Object> wrapper = new LinkedHashMap<>();
            wrapper.put("description", description);
            if (result != null) {
                @SuppressWarnings("unchecked")
                Map<String, Object> resultMap = objectMapper.convertValue(result, Map.class);
                wrapper.put("data", resultMap);
            }
            return objectMapper.writeValueAsString(wrapper);
        } catch (Exception e) {
            return "{\"description\": \"" + description.replace("\"", "'") + "\"}";
        }
    }
}
