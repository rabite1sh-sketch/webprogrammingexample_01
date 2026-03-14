package com.fanpage.ten_cm.controller;

import com.fanpage.ten_cm.entity.Donation;
import com.fanpage.ten_cm.entity.Item;
import com.fanpage.ten_cm.entity.User;
import com.fanpage.ten_cm.repository.DonationRepository;
import com.fanpage.ten_cm.repository.ItemRepository;
import com.fanpage.ten_cm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin(origins = "*")
@Controller
public class DonationController {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @GetMapping("/donate/item1")
    public String donateItem1Page(Model model) {
        bindItemDetail(model, 0);
        return "donate_item1";
    }

    @GetMapping("/donate/item2")
    public String donateItem2Page(Model model) {
        bindItemDetail(model, 1);
        return "donate_item2";
    }

    @GetMapping("/donate/item3")
    public String donateItem3Page(Model model) {
        bindItemDetail(model, 2);
        return "donate_item3";
    }

    private void bindItemDetail(Model model, int index) {
        List<Item> items = itemRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        Item item = (items.size() > index) ? items.get(index) : null;
        model.addAttribute("item", item);
    }

    @PostMapping("/donate")
    @ResponseBody
    @Transactional
    public Map<String, Object> addDonation(@RequestBody Map<String, Object> requestData) {
        Map<String, Object> response = new HashMap<>();

        try {
            System.out.println("\n=== 💸 기부 저장 요청 도달 ===");
            System.out.println("프론트에서 온 데이터: " + requestData);

            if (!requestData.containsKey("amount") || !requestData.containsKey("user_id")) {
                System.out.println("❌ 데이터 누락: amount 또는 user_id가 없습니다.");
                response.put("success", false);
                response.put("message", "전달된 데이터가 부족합니다.");
                return response;
            }

            int amount = Integer.parseInt(requestData.get("amount").toString());
            String userId = (String) requestData.get("user_id");

            Optional<User> optionalUser = userRepository.findById(userId);

            if (optionalUser.isPresent()) {
                Donation donation = new Donation();
                donation.setAmount(amount);
                donation.setUser(optionalUser.get());

                donationRepository.save(donation);

                System.out.println("✅ DB 저장 성공! 금액: " + amount + "원 / 유저: " + userId);
                response.put("success", true);
            } else {
                System.out.println("❌ 유저 찾기 실패! 요청된 아이디: " + userId);
                response.put("success", false);
                response.put("message", "로그인한 유저 정보를 DB에서 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            System.err.println("🔥 기부 처리 중 서버 에러 발생!");
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "결제 처리 중 에러가 발생했습니다.");
        }

        return response;
    }

    @GetMapping("/donation")
    @ResponseBody
    public Map<String, Object> getTotalDonation() {
        Map<String, Object> response = new HashMap<>();
        try {
            Integer totalResult = donationRepository.sumTotalAmount();
            int total = (totalResult != null) ? totalResult : 0;
            response.put("total_amount", total);
            response.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("total_amount", 0);
            response.put("success", false);
        }
        return response;
    }
}
