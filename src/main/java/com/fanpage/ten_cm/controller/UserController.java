package com.fanpage.ten_cm.controller;

import com.fanpage.ten_cm.entity.User;
import com.fanpage.ten_cm.service.EmailVerificationService;
import com.fanpage.ten_cm.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailVerificationService emailVerificationService;

    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (!emailVerificationService.isEmailVerified(user.getEmail())) {
                throw new IllegalStateException("이메일 인증이 완료되지 않았습니다. 인증 링크 또는 인증번호 확인을 먼저 진행해 주세요.");
            }

            userService.registerUser(user);
            emailVerificationService.consumeVerifiedEmail(user.getEmail());
            response.put("success", true);
            response.put("message", "회원가입이 완료되었습니다.");
        } catch (IllegalStateException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "서버 오류가 발생했습니다.");
        }

        return response;
    }

    @DeleteMapping("/users/{id}")
    public Map<String, Object> withdraw(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();

        try {
            userService.deleteUser(id);
            response.put("success", true);
            response.put("message", "회원 탈퇴가 완료되었습니다. 그동안 이용해주셔서 감사합니다.");
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "탈퇴 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        return response;
    }
}
