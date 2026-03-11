package fptu.sba301.ats.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private UserInfo user;

    @Data
    @Builder
    public static class UserInfo {
        private UUID id;
        private String email;
        private String fullName;
        private String role;
        private String avatarURL;
    }
}
