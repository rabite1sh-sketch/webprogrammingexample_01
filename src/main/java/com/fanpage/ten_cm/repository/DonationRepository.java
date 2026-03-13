package com.fanpage.ten_cm.repository;

import com.fanpage.ten_cm.entity.Donation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    // ⭐️ 핵심 수정: 테이블의 모든 기부 금액(amount)을 합산하는 쿼리입니다.
    // 데이터가 하나도 없을 경우 null을 반환할 수 있으므로 반환 타입을 꼭 Integer로 설정해야 합니다.
    @Query("SELECT SUM(d.amount) FROM Donation d")
    Integer sumTotalAmount();
    
}