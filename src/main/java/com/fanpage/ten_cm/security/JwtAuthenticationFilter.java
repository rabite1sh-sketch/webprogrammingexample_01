package com.fanpage.ten_cm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 1. 요청 헤더에서 토큰을 꺼내옵니다. (보통 Authorization: Bearer <토큰> 형태)
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // "Bearer " 뒷부분만 잘라냄

            // 2. 토큰이 유효한지 확인합니다.
            if (jwtTokenProvider.validateToken(token)) {
                String userId = jwtTokenProvider.getUserId(token);
                
                // 3. 토큰이 맞으면 "이 유저는 인증된 사람이다"라고 시스템에 등록합니다.
                UsernamePasswordAuthenticationToken auth = 
                    new UsernamePasswordAuthenticationToken(userId, null, null);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        // 4. 다음 검사 단계로 넘겨줍니다.
        filterChain.doFilter(request, response);
    }
}