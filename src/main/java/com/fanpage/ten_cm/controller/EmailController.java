package com.fanpage.ten_cm.controller;

import com.fanpage.ten_cm.service.EmailVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "*")
@Controller
@RequiredArgsConstructor
public class EmailController {

    private final EmailVerificationService emailVerificationService;

    @ResponseBody
    @PostMapping("/send-verification")
    public Map<String, Object> sendVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        Map<String, Object> response = new HashMap<>();
        try {
            emailVerificationService.sendVerificationMail(email);
            response.put("success", true);
            response.put("message", "인증번호와 인증 링크를 발송했습니다. 메일함을 확인해 주세요.");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }

    @ResponseBody
    @PostMapping("/verify-code")
    public Map<String, Object> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String inputCode = request.get("code");

        Map<String, Object> response = new HashMap<>();
        boolean verified = emailVerificationService.verifyCode(email, inputCode);

        if (verified) {
            response.put("success", true);
            response.put("message", "이메일 인증이 완료되었습니다.");
        } else {
            response.put("success", false);
            response.put("message", "인증번호가 틀렸습니다. 다시 확인해 주세요.");
        }

        return response;
    }

    @GetMapping("/verify-email")
    public String verifyEmailByLink(@RequestParam("token") String token, Model model) {
        boolean verified = emailVerificationService.verifyByToken(token);
        model.addAttribute("verified", verified);
        model.addAttribute("message", verified ? "이메일 인증이 완료되었습니다. 회원가입을 계속 진행해 주세요."
            : "인증 링크가 유효하지 않거나 만료되었습니다.");
        return "email_verified";
    }
}
