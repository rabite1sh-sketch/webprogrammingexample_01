package com.fanpage.ten_cm.repository;

import com.fanpage.ten_cm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    
    // 아이디가 존재하는지 확인하는 마법의 메서드!
    // 만약 기본키(PK)가 id라면 JpaRepository에 기본적으로 existsById()가 있지만, 
    // 명시적으로 적어두는 것도 좋습니다.
    boolean existsById(String id); 
}