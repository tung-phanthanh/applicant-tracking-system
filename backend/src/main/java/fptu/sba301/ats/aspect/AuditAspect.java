package fptu.sba301.ats.aspect;

import fptu.sba301.ats.annotation.LogAudit;
import fptu.sba301.ats.security.UserPrincipal;
import fptu.sba301.ats.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogService auditLogService;

    @AfterReturning(pointcut = "@annotation(logAudit)", returning = "result")
    public void auditLog(JoinPoint joinPoint, LogAudit logAudit, Object result) {
        String action = logAudit.action();
        String resource = logAudit.resource();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UUID userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            userId = ((UserPrincipal) authentication.getPrincipal()).getId();
        }

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");

        Map<String, Object> newValue = new HashMap<>();
        if (result != null) {
            newValue.put("response", result);
        }

        // Get method arguments for more context
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Object[] args = joinPoint.getArgs();
        Map<String, Object> params = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (!(args[i] instanceof HttpServletRequest) && !(args[i] instanceof UserPrincipal)) {
                params.put(parameterNames[i], args[i]);
            }
        }
        newValue.put("parameters", params);

        auditLogService.logAction(
                userId,
                action,
                resource,
                null, // Entity ID could be extracted from result or params if needed
                null,
                newValue,
                ipAddress,
                userAgent
        );
    }
}
