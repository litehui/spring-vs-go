package com.compare.favorite.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "favorite_product", uniqueConstraints = @UniqueConstraint(columnNames = {"buyerId", "productId"}))
public class FavoriteProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long buyerId;
    @Column(nullable = false)
    private Long productId;
    private LocalDateTime createdAt;
}
