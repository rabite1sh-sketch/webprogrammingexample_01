package com.fanpage.ten_cm.repository;

import com.fanpage.ten_cm.entity.Comment; // 👈 본인의 실제 패키지 이름으로 맞췄습니다!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 기존 server.js에 있던 "본인만 삭제할 수 있는 기능"을 위한 맞춤형 메서드입니다!
    void deleteByIdAndUserId(Long id, String userId);
}