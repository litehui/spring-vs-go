package com.compare.shop.repository;

import com.compare.shop.entity.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShopRepository extends JpaRepository<Shop, Long> {
    List<Shop> findBySellerId(Long sellerId);
}
