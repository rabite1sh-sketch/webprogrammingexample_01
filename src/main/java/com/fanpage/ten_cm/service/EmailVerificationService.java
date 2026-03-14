package com.fanpage.ten_cm.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();
    private final Map<String, String> verificationTokens = new ConcurrentHashMap<>();
    private final Set<String> verifiedEmails = ConcurrentHashMap.newKeySet();

    public EmailVerificationService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationMail(String email) {
        String code = String.format("%06d", new Random().nextInt(999999));
        String token = UUID.randomUUID().toString();

        verificationCodes.put(email, code);
        verificationTokens.put(token, email);

        String verifyLink = "http://localhost:8080/verify-email?token=" + token;

        Context context = new Context();
        context.setVariable("email", email);
        context.setVariable("code", code);
        context.setVariable("verifyLink", verifyLink);

        String html = templateEngine.process("emails/verification-mail", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, StandardCharsets.UTF_8.name());
            helper.setFrom("rabite1sh@gmail.com");
            helper.setTo(email);
            helper.setSubject("🌼 10cm 팬페이지 이메일 인증 안내");
            helper.setText(html, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("이메일 발송에 실패했습니다: " + e.getMessage());
        }
    }

    public boolean verifyCode(String email, String inputCode) {
        String savedCode = verificationCodes.get(email);
        if (savedCode != null && savedCode.equals(inputCode)) {
            verifiedEmails.add(email);
            verificationCodes.remove(email);
            return true;
        }
        return false;
    }

    public boolean verifyByToken(String token) {
        String email = verificationTokens.remove(token);
        if (email == null) {
            return false;
        }
        verifiedEmails.add(email);
        verificationCodes.remove(email);
        return true;
    }

    public boolean isEmailVerified(String email) {
        return email != null && verifiedEmails.contains(email);
    }

    public void consumeVerifiedEmail(String email) {
        if (email != null) {
            verifiedEmails.remove(email);
        }
    }
}
