package fptu.sba301.ats.aspect;

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
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @AfterReturning(pointcut = "@annotation(logAudit)", returning = "result")
    public void logAuditActivity(JoinPoint joinPoint, LogAudit logAudit, Object result) {
        try {
            String ipAddress = "Unknown";
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = request.getRemoteAddr();
            }

            String username = "System";
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                username = ((UserDetails) authentication.getPrincipal()).getUsername();
            } else if (authentication != null && authentication.getPrincipal() instanceof String) {
                username = (String) authentication.getPrincipal(); // might be "anonymousUser"
            }

            User user = userRepository.findByEmailAndDeletedFalse(username).orElse(null);

            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            String methodName = signature.getMethod().getName();

            // Extract targeted resource (from annotation or fall back to class name)
            String resource = logAudit.resource();
            if (resource.isEmpty()) {
                resource = joinPoint.getTarget().getClass().getSimpleName();
                if (resource.endsWith("ServiceImpl")) {
                    resource = resource.substring(0, resource.length() - 11);
                }
            }

            // Provide a brief detail string
            String detail = String.format("Action '%s' performed on '%s' via %s",
                    logAudit.action(), resource, methodName);

            AuditLog auditLog = AuditLog.builder()
                    .userId(user != null ? user.getId() : null)
                    .action(logAudit.action())
                    .entityType(resource)
                    // At this level we don't naturally have the entity ID unless we inspect the
                    // result object.
                    // For the mock, we can just save it as null or try to extract it from 'result'
                    // via reflection.
                    .entityId(null)
                    .ipAddress(ipAddress)
                    .userAgent(attributes != null ? attributes.getRequest().getHeader("User-Agent") : "Unknown")
                    // Note: `createdAt` is populated via @PrePersist on the entity
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Audit log recorded: {} by {}", detail, username);

        } catch (Exception e) {
            log.error("Failed to save audit log for method: {}", joinPoint.getSignature().toShortString(), e);
        }
    }
}
