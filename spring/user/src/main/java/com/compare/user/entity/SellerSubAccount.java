package com.compare.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "seller_sub_account")
public class SellerSubAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Long sellerId;
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(length = 100)
    private String nickname;
    @Column(length = 50)
    private String role = "operator";
    @Column(columnDefinition = "TEXT[]")
    private String[] permissions;
    private Short status = 1;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
