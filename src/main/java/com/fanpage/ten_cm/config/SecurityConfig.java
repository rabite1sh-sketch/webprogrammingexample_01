package com.fanpage.ten_cm.config;

import com.fanpage.ten_cm.security.JwtAuthenticationFilter;
import com.fanpage.ten_cm.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // 세션(장부)은 사용하지 않겠다고 선언
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 권한 설정 시작!
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/", "/login", "/signup", "/join", 
                    "/css/**", "/js/**", "/images/**", 
                    "/favicon.ico", "/error",
                    
                    // 🔥 프론트엔드가 화면을 그리기 위해 요청하는 데이터 주소들 (프리패스!)
                    "/items", "/items/**",        // 기부 품목 리스트
                    "/item/**",                    // 기부 품목 상세 페이지
                    "/donation",                  // 현재 기부 총액
                    "/comments", "/comments/**",  // 응원 메시지 리스트
                    
                    // 🔥 이메일 인증 관련 주소 (토큰이 없어도 할 수 있어야 함)
                    "/send-verification", "/verify-code"
                ).permitAll()
                
                // 나머지는 무조건 토큰(인증) 필요! (예: 기부 결제, 관리자 기능 등)
                .anyRequest().authenticated() 
            )
            // 토큰 검사원(Filter) 배치
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    // ★ 범용 이름표(PasswordEncoder)로 암호화 기계를 스프링에 등록합니다.
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}