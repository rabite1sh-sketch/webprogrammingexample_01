package com.fanpage.ten_cm.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "comments")
@Getter
@Setter
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore 
    private User user; 

    private String name;
    private String text;
    private String time; 

    // 🔥 핵심 해결 코드: 함수 이름을 getWriterId로 바꿔서 DB 에러(충돌)를 피합니다!
    // @Transient를 붙이면 DB는 이 코드를 신경 쓰지 않고 패스합니다.
    @Transient
    @JsonProperty("userId") // 하지만 프론트엔드로는 여전히 "userId"라는 이름으로 전달됩니다!
    public String getWriterId() {
        return this.user != null ? this.user.getId() : null;
    }
}