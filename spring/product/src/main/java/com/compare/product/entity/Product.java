package com.compare.product.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long shopId;
    @Column(nullable = false)
    private Long sellerId;
    @Column(nullable = false, length = 200)
    private String name;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    @Column(nullable = false)
    private Integer stock = 0;
    @Column(length = 100)
    private String category;
    @Column(columnDefinition = "TEXT[]")
    private String[] images;
    private Short status = 1;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
