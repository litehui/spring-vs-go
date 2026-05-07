package com.compare.shop.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "shop")
public class Shop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long sellerId;
    @Column(nullable = false, length = 200)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(length = 500)
    private String logo;
    @Column(length = 100)
    private String category;
    private Short status = 1;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
