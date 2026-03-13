package com.fanpage.ten_cm.controller;

import com.fanpage.ten_cm.entity.User;
import com.fanpage.ten_cm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController // 프론트가 원하는 JSON으로 대답합니다!
@CrossOrigin(origins = "*") // CORS 차단 에러 방지
public class UserController {

    @Autowired
    private UserService userService;

    // ================= [ 1. 회원 가입 ] =================
    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            userService.registerUser(user);
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

    // ================= [ 2. 회원 탈퇴 ] =================
    @DeleteMapping("/users/{id}")
    public Map<String, Object> withdraw(@PathVariable("id") String id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            userService.deleteUser(id); // 서비스 계층에 삭제 요청!
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