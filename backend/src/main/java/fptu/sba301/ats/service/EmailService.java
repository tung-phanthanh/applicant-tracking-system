package fptu.sba301.ats.service;

public interface EmailService {
    /**
     * Send an account activation email with a link to set the password.
     * The link will point to: {frontendUrl}/activate?token={token}
     */
    void sendActivationEmail(String to, String fullName, String token);

    /**
     * Send a password reset email with a link.
     * The link will point to: {frontendUrl}/reset-password?token={token}
     */
    void sendPasswordResetEmail(String to, String fullName, String token);
}
