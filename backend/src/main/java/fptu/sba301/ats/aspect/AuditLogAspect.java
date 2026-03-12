package fptu.sba301.ats.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import fptu.sba301.ats.entity.AuditLog;
import fptu.sba301.ats.repository.AuditLogRepository;
import fptu.sba301.ats.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditLogAspect {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    @Pointcut("execution(* fptu.sba301.ats.service.impl.OfferServiceImpl.*(..)) " +
            "|| execution(* fptu.sba301.ats.service.impl.ScorecardTemplateServiceImpl.*(..)) " +
            "|| execution(* fptu.sba301.ats.service.impl.OnboardingServiceImpl.*(..))")
    public void targetServices() {
    }

    @Around("targetServices()")
    public Object logAudit(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();

        // Skip read-only methods
        if (methodName.startsWith("get") || methodName.startsWith("find") || methodName.startsWith("list")) {
            return joinPoint.proceed();
        }

        Object result = null;
        try {
            result = joinPoint.proceed();

            // Log successful execution
            try {
                String email = "system";
                try {
                    email = SecurityUtils.getCurrentUserEmail();
                } catch (Exception e) {
                    // Not authenticated, fallback to "system"
                }

                String entityType = joinPoint.getTarget().getClass().getSimpleName().replace("ServiceImpl", "");
                String action = methodName;

                String argsJson;
                try {
                    argsJson = objectMapper.writeValueAsString(joinPoint.getArgs());
                } catch (Exception e) {
                    argsJson = "{\"error\": \"Could not serialize arguments\"}";
                }

                AuditLog logEntry = AuditLog.builder()
                        .actorEmail(email)
                        .action(action)
                        .entityType(entityType)
                        .newValue(argsJson) // Using new_value to store method arguments detail
                        .build();

                auditLogRepository.save(logEntry);
                log.info("Audit log saved: {} performed {} on {}", email, action, entityType);
            } catch (Exception e) {
                log.error("Failed to save audit log for {}", methodName, e);
            }

            return result;
        } catch (Throwable t) {
            throw t; // Don't log on error, or you could also log failed attempts here
        }
    }
}
