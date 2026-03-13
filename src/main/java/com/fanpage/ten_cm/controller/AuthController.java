package com.fanpage.ten_cm.controller;

import com.fanpage.ten_cm.dto.LoginRequest;
import com.fanpage.ten_cm.security.JwtTokenProvider;
import com.fanpage.ten_cm.repository.UserRepository;
import com.fanpage.ten_cm.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, JwtTokenProvider jwtTokenProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest request) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 1. DB에서 아이디로 유저 찾기
            User user = userRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 ID입니다."));

            // 2. 비밀번호 검증
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new IllegalArgumentException("잘못된 비밀번호입니다.");
            }

            // 3. 토큰 발행
            String token = jwtTokenProvider.createToken(user.getId(), user.getRole());

            // 4. 성공 응답
            response.put("success", true);
            response.put("token", token);
            response.put("user", user);

        } catch (IllegalArgumentException e) {
            // 🔥 여기서 잡아줘야 undefined가 안 뜨고 메시지가 나갑니다!
            response.put("success", false);
            response.put("message", e.getMessage()); 
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "로그인 중 서버 오류가 발생했습니다.");
        }

        return response;
    }
} 