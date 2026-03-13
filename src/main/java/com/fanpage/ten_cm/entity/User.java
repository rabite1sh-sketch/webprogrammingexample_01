package com.fanpage.ten_cm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users") // DB의 users 테이블과 연결
@Getter
@Setter
public class User {

    @Id // 기본키(PK)
    @Column(unique = true, nullable = false)
    private String id; 
    
    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String password;
    
    private String email; 
    
    private String role; 

    // 🔥 핵심 추가: 회원이 탈퇴할 때(User 삭제), 이 회원의 기부(Donation) 내역도 같이 지워지도록 연쇄 삭제(Cascade) 설정!
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Donation> donations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

} // 🔥 이 중괄호가 빠져서 에러가 났을 확률이 높습니다!