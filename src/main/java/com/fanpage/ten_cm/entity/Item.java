package com.fanpage.ten_cm.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private int price;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false)
    private String donationTarget;

    @Column(columnDefinition = "TEXT")
    private String donationDescription;
}
