package fptu.sba301.ats.service.impl;

import fptu.sba301.ats.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
@Log4j2
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Value("${app.mail.from}")
    private String fromAddress;

    // ── Public API ────────────────────────────────────────────────────────────

    @Override
    @Async
    public void sendActivationEmail(String to, String fullName, String token) {
        String link = frontendUrl + "/activate?token=" + token;
        String subject = "Activate your ATS account";
        String body = buildActivationEmailHtml(fullName, link);
        send(to, subject, body);
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String to, String fullName, String token) {
        String link = frontendUrl + "/reset-password?token=" + token;
        String subject = "Reset your ATS password";
        String body = buildPasswordResetEmailHtml(fullName, link);
        send(to, subject, body);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void send(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {} — subject: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            // We swallow the exception so a mail failure doesn't break the user-facing flow.
            // In production you may want to push to a retry queue.
        }
    }

    private String buildActivationEmailHtml(String fullName, String link) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif; background: #f6f8fa; margin: 0; padding: 0;">
                  <div style="max-width: 520px; margin: 40px auto; background: #fff; border-radius: 10px;
                              border: 1px solid #e1e4e8; padding: 36px 40px;">
                    <h2 style="color: #1a1a2e; margin-top: 0;">Welcome to Enterprise ATS 👋</h2>
                    <p style="color: #444; line-height: 1.6;">
                      Hi <strong>%s</strong>,<br><br>
                      An account has been created for you. Click the button below to activate your account
                      and set your password. This link expires in <strong>24 hours</strong>.
                    </p>
                    <div style="text-align: center; margin: 32px 0;">
                      <a href="%s"
                         style="background: #4f46e5; color: #fff; text-decoration: none;
                                padding: 14px 32px; border-radius: 8px; font-size: 16px; font-weight: 600;">
                        Activate Account
                      </a>
                    </div>
                    <p style="color: #888; font-size: 13px; line-height: 1.5;">
                      Or copy and paste this link into your browser:<br>
                      <a href="%s" style="color: #4f46e5;">%s</a>
                    </p>
                    <hr style="border: none; border-top: 1px solid #e1e4e8; margin: 24px 0;">
                    <p style="color: #aaa; font-size: 12px;">
                      If you did not expect this email, you can safely ignore it.
                    </p>
                  </div>
                </body>
                </html>
                """.formatted(fullName, link, link, link);
    }

    private String buildPasswordResetEmailHtml(String fullName, String link) {
        return """
                <!DOCTYPE html>
                <html>
                <body style="font-family: Arial, sans-serif; background: #f6f8fa; margin: 0; padding: 0;">
                  <div style="max-width: 520px; margin: 40px auto; background: #fff; border-radius: 10px;
                              border: 1px solid #e1e4e8; padding: 36px 40px;">
                    <h2 style="color: #1a1a2e; margin-top: 0;">Password Reset Request 🔐</h2>
                    <p style="color: #444; line-height: 1.6;">
                      Hi <strong>%s</strong>,<br><br>
                      We received a request to reset your password. Click the button below to choose a new password.
                      This link expires in <strong>15 minutes</strong>.
                    </p>
                    <div style="text-align: center; margin: 32px 0;">
                      <a href="%s"
                         style="background: #4f46e5; color: #fff; text-decoration: none;
                                padding: 14px 32px; border-radius: 8px; font-size: 16px; font-weight: 600;">
                        Reset Password
                      </a>
                    </div>
                    <p style="color: #888; font-size: 13px; line-height: 1.5;">
                      Or copy and paste this link into your browser:<br>
                      <a href="%s" style="color: #4f46e5;">%s</a>
                    </p>
                    <hr style="border: none; border-top: 1px solid #e1e4e8; margin: 24px 0;">
                    <p style="color: #aaa; font-size: 12px;">
                      If you didn't request a password reset, you can safely ignore this email.
                      Your password won't change until you click the link above.
                    </p>
                  </div>
                </body>
                </html>
                """.formatted(fullName, link, link, link);
    }
}
