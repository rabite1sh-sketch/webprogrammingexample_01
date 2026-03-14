package com.fanpage.ten_cm.dto;

public class ItemRequestDto {
    private String title;
    private String content;
    private int price;
    private String imageUrl;
    private String donationTarget;
    private String donationDescription;

    public ItemRequestDto() {
    }

    public ItemRequestDto(String title, String content, int price, String imageUrl, String donationTarget, String donationDescription) {
        this.title = title;
        this.content = content;
        this.price = price;
        this.imageUrl = imageUrl;
        this.donationTarget = donationTarget;
        this.donationDescription = donationDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public String getDonationTarget() {
        return donationTarget;
    }

    public void setDonationTarget(String donationTarget) {
        this.donationTarget = donationTarget;
    }

    public String getDonationDescription() {
        return donationDescription;
    }

    public void setDonationDescription(String donationDescription) {
        this.donationDescription = donationDescription;
    }
}
