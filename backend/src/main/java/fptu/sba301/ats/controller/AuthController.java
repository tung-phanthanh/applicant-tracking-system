package fptu.sba301.ats.controller;

import fptu.sba301.ats.dto.request.LoginRequest;
import fptu.sba301.ats.dto.request.RefreshTokenRequest;
import fptu.sba301.ats.dto.response.AuthResponse;
import fptu.sba301.ats.service.AuthService;
import fptu.sba301.ats.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static fptu.sba301.ats.constant.AppConstant.AUTH_CONTROLLER_URL;
import static fptu.sba301.ats.constant.AppConstant.BASE_URL;

@RestController
@RequestMapping(BASE_URL + AUTH_CONTROLLER_URL)
@RequiredArgsConstructor
public class AuthController {
    private final RefreshTokenService refreshTokenService;
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @RequestBody LoginRequest request) {

        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            @RequestBody RefreshTokenRequest request) {

        refreshTokenService.logout(request.getRefreshToken());
        return ResponseEntity.ok("Logged out successfully");
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(
            @RequestBody RefreshTokenRequest request) {

        return ResponseEntity.ok(
                refreshTokenService.refresh(request.getRefreshToken())
        );
    }

}
