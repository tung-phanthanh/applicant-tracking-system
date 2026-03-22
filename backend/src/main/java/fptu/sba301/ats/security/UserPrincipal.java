package fptu.sba301.ats.security;

import fptu.sba301.ats.entity.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
public class UserPrincipal implements UserDetails {
    private final UUID id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    public static UserPrincipal create(User user) {
        java.util.Set<GrantedAuthority> authorities = new java.util.HashSet<>();
        
        // Add Role (with and without ROLE_ prefix for maximum compatibility with hasRole/hasAuthority)
        if (user.getRole() != null) {
            String roleName = user.getRole().getName().name();
            authorities.add(new SimpleGrantedAuthority(roleName));
            if (!roleName.startsWith("ROLE_")) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
            }
            
            // Add Permissions
            if (user.getRole().getPermissions() != null) {
                user.getRole().getPermissions().forEach(permission -> 
                    authorities.add(new SimpleGrantedAuthority(permission.getKey()))
                );
            }
        }

        return UserPrincipal.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPasswordHash())
                .authorities(authorities)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
