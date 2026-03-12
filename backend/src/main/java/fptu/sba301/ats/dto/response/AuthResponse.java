package fptu.sba301.ats.dto.response;

import fptu.sba301.ats.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

import java.util.UUID;

@Getter
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UUID userId;
    private String fullName;
    private String email;
    private Role role;
}
