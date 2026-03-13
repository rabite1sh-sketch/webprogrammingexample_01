package com.fanpage.ten_cm.controller;

import com.fanpage.ten_cm.entity.Item;
import com.fanpage.ten_cm.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fanpage.ten_cm.dto.ItemRequestDto;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*") 
public class ItemController {

    private final ItemRepository itemRepository;

    // ==========================================
    // 0. 기존에 있던 전체 조회 (GET)
    // ==========================================
    @GetMapping("/items")
    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    // ==========================================
    // 1. 기부 품목 추가 (POST) - 실제 DB 연동
    // ==========================================
    @PostMapping("/items")
    public ResponseEntity<?> addItem(@RequestBody ItemRequestDto request) {
        try {
            Item newItem = new Item();
            newItem.setName(request.getName());
            newItem.setPrice(request.getPrice());
            newItem.setImageUrl(request.getImageUrl());
            
            itemRepository.save(newItem); // DB에 저장!

            return ResponseEntity.ok().body(Map.of("success", true, "message", "추가 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("품목 추가 실패: " + e.getMessage());
        }
    }

    // ==========================================
    // 2. 기부 품목 수정 (PUT) - 실제 DB 연동
    // ==========================================
    @PutMapping("/items/{id}")
    public ResponseEntity<?> updateItem(@PathVariable("id") Long id, @RequestBody ItemRequestDto request) {
        try {
            Optional<Item> optionalItem = itemRepository.findById(id);
            if (optionalItem.isPresent()) {
                Item item = optionalItem.get();
                item.setName(request.getName());
                item.setPrice(request.getPrice());
                
                itemRepository.save(item); // 수정된 내용 DB에 덮어쓰기!

                return ResponseEntity.ok().body(Map.of("success", true, "message", "수정 완료"));
            } else {
                return ResponseEntity.status(404).body("해당 품목을 찾을 수 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("품목 수정 실패: " + e.getMessage());
        }
    }

    // ==========================================
    // 3. 기부 품목 삭제 (DELETE) - 실제 DB 연동
    // ==========================================
    @DeleteMapping("/items/{id}")
    public ResponseEntity<?> deleteItem(@PathVariable("id") Long id) {
        try {
            itemRepository.deleteById(id); // DB에서 삭제!
            return ResponseEntity.ok().body(Map.of("success", true, "message", "삭제 완료"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("품목 삭제 실패: " + e.getMessage());
        }
    }
}