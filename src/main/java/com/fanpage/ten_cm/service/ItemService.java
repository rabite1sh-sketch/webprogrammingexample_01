package com.fanpage.ten_cm.service;

import com.fanpage.ten_cm.entity.Item;
import com.fanpage.ten_cm.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    public Item findById(Long id) {
        return itemRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("해당 ID의 품목을 찾을 수 없습니다. id=" + id));
    }
}
