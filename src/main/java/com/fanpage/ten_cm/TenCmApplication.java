package com.fanpage.ten_cm;

import com.fanpage.ten_cm.entity.User;
import com.fanpage.ten_cm.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder; // 🔥 BCrypt 대신 더 넓은 의미의 인터페이스로 변경

@SpringBootApplication
public class TenCmApplication {

    public static void main(String[] args) {
        SpringApplication.run(TenCmApplication.class, args);
    }

    // ================= [ 관리자 계정 자동 생성 (암호화 적용) ] =================
    @Bean
    public CommandLineRunner initAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // 🔥 SecurityConfig에 이미 만들어진 기계를 알아서 가져와서 씁니다!
            String encodedAdminPw = passwordEncoder.encode("admin");

            // 만약 admin 아이디가 없으면 새로 만듭니다.
            if (!userRepository.existsById("admin")) {
                User admin = new User();
                admin.setId("admin");
                admin.setPassword(encodedAdminPw); 
                admin.setName("관리자");
                admin.setRole("ROLE_ADMIN");
                userRepository.save(admin);
                System.out.println("✅ 관리자 계정(admin/admin)이 생성되었습니다. (비밀번호 암호화 완료 🛡️)");
            } else {
                // 이미 있다면 암호화된 비밀번호로 갱신
                User admin = userRepository.findById("admin").get();
                admin.setPassword(encodedAdminPw); 
                userRepository.save(admin);
                System.out.println("🔄 기존 관리자 계정 비밀번호가 암호화되어 초기화되었습니다. 🛡️");
            }
        };
    }
}