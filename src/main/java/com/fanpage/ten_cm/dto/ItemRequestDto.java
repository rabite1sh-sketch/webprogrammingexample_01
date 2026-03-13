package com.fanpage.ten_cm.dto;

/**
 * 화면에서 보내주는 기부 품목 정보를 담는 그릇(DTO)입니다.
 */
public class ItemRequestDto {
    private String name;
    private int price;
    private String imageUrl;

    // 1. 기본 생성자 (이게 없으면 스프링이 데이터를 못 담을 때가 많아요!)
    public ItemRequestDto() {
    }

    // 2. 모든 필드를 가진 생성자 (선택사항이지만 안전함)
    public ItemRequestDto(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // 3. Getter & Setter (이 이름들이 Controller의 메서드와 정확히 일치해야 합니다)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}