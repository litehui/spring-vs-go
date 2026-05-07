package com.compare.favorite.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "favorite_shop", uniqueConstraints = @UniqueConstraint(columnNames = {"buyerId", "shopId"}))
public class FavoriteShop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long buyerId;
    @Column(nullable = false)
    private Long shopId;
    private LocalDateTime createdAt;
}
