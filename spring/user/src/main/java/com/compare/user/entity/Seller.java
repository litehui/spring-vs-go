package com.compare.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "seller")
public class Seller {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(length = 200)
    private String companyName;
    @Column(length = 100)
    private String contactName;
    @Column(length = 20)
    private String phone;
    @Column(length = 100)
    private String email;
    @Column(length = 100)
    private String businessLicense;
    private Short status = 1;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
