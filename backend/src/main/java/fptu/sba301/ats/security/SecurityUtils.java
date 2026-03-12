package fptu.sba301.ats.security;

import fptu.sba301.ats.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || !(auth.getPrincipal() instanceof UserDetails)) {
            throw new BusinessException( "No authenticated user found", HttpStatus.UNAUTHORIZED);
        }
        return ((UserDetails) auth.getPrincipal()).getUsername();
    }
}
