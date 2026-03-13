package com.fanpage.ten_cm.service;

import com.fanpage.ten_cm.entity.User;
import com.fanpage.ten_cm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ================= [ 1. 회원 가입 로직 ] =================
    @Transactional
    public void registerUser(User user) {
        // 이미 있는 아이디인지 검사
        if (userRepository.existsById(user.getId())) {
            throw new IllegalStateException("이미 존재하는 아이디입니다.");
        }
        
        // 비밀번호 암호화 (스프링 시큐리티 필수)
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);

        // 기본 권한 설정 (빈 값으로 오면 기본 유저 권한 부여)
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("ROLE_USER");
        }

        userRepository.save(user);
    }

    // ================= [ 2. 회원 탈퇴 로직 ] =================
    @Transactional
    public void deleteUser(String id) {
        // 지울 유저가 진짜 있는지 확인
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("존재하지 않는 사용자입니다.");
        }
        
        // DB에서 유저 완전 삭제! (Hard Delete)
        userRepository.deleteById(id);
    }
}