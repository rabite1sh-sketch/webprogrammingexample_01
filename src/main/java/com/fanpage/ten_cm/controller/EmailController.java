package com.fanpage.ten_cm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@CrossOrigin(origins = "*")
@RestController
public class EmailController {

    @Autowired
    private JavaMailSender mailSender; // 스프링이 준비한 마법의 우체부 아저씨!

    // 이메일과 인증번호를 임시로 기억해둘 메모장 (실무에서는 Redis 같은 캐시를 씁니다)
    private Map<String, String> verificationCodes = new HashMap<>();

    // 1. 인증번호 발송 (기존: app.post('/send-verification'))
    @PostMapping("/send-verification")
    public Map<String, Object> sendVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        // 1-1. 6자리 랜덤 인증번호 만들기
        String code = String.format("%06d", new Random().nextInt(999999));
        
        // 1-2. 메모장에 적어두기 (예: "test@gmail.com" -> "123456")
        verificationCodes.put(email, code);

        // 1-3. 진짜 이메일 보내기

        // 1-3. 진짜 이메일 보내기
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("rabite1sh@gmail.com"); // <-- 오류 나서 추가 
        message.setTo(email);
        message.setSubject("🌼 10cm 팬페이지 회원가입 인증번호입니다.");
        message.setText("안녕하세요! 인증번호는 [ " + code + " ] 입니다.\n팬페이지 화면에 입력해 주세요!");
        
        mailSender.send(message);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "인증번호가 발송되었습니다! 이메일을 확인해주세요.");
        return response;
    }

    // 2. 인증번호 확인 (기존: app.post('/verify-code'))
    @PostMapping("/verify-code")
    public Map<String, Object> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String inputCode = request.get("code");
        
        Map<String, Object> response = new HashMap<>();
        
        // 메모장에 적힌 코드랑, 유저가 입력한 코드가 똑같은지 확인!
        if (verificationCodes.containsKey(email) && verificationCodes.get(email).equals(inputCode)) {
            response.put("success", true);
            verificationCodes.remove(email); // 인증 성공했으니 메모장에서 지우기
        } else {
            response.put("success", false);
            response.put("message", "인증번호가 틀렸습니다. 다시 확인해 주세요.");
        }
        
        return response;
    }
}