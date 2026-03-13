package com.fanpage.ten_cm.controller;

import com.fanpage.ten_cm.entity.Comment;
import com.fanpage.ten_cm.entity.User;
import com.fanpage.ten_cm.repository.CommentRepository;
import com.fanpage.ten_cm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*") 
@RestController 
@RequestMapping("/comments") 
public class CommentController {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    // 1. 댓글 조회
    @GetMapping
    public List<Comment> getComments() {
        return commentRepository.findAll(); 
    }

    // 2. 댓글 작성
    @PostMapping 
    public Comment addComment(@RequestBody Map<String, Object> requestData) {
        Comment comment = new Comment();
        
        // 프론트엔드에서 보낸 내용 저장
        comment.setName((String) requestData.get("name"));
        comment.setText((String) requestData.get("text"));
        comment.setTime((String) requestData.get("time"));

        // 유저 정보 찾아서 연결
        String userId = (String) requestData.get("userId");
        if (userId != null) {
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                comment.setUser(optionalUser.get()); 
            }
        }

        return commentRepository.save(comment);
    }

    // 3. 댓글 삭제 (🔥 관리자 마스터 키 추가 완료!)
    @Transactional 
    @DeleteMapping("/{id}")
    public String deleteComment(
            @PathVariable Long id, 
            @RequestParam("user_id") String userId 
    ) {
        // 1. 삭제를 요청한 사람이 누군지 찾습니다.
        Optional<User> optionalUser = userRepository.findById(userId);
        
        // 2. 그 사람이 존재하고, 역할이 "ROLE_ADMIN"(관리자)인지 확인합니다.
        if (optionalUser.isPresent() && "ROLE_ADMIN".equals(optionalUser.get().getRole())) {
            // 👮‍♂️ 관리자라면 조건 없이 해당 번호(id)의 댓글 삭제!
            commentRepository.deleteById(id);
        } else {
            // 🧑 일반 유저라면 기존처럼 '해당 번호(id) + 내 아이디(userId)'가 일치할 때만 삭제!
            commentRepository.deleteByIdAndUserId(id, userId); 
        }
        
        return "{\"message\": \"삭제 성공\"}";
    }
}