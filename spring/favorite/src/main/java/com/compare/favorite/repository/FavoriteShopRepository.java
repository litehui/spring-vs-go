package com.compare.favorite.repository;

import com.compare.favorite.entity.FavoriteShop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FavoriteShopRepository extends JpaRepository<FavoriteShop, Long> {
    List<FavoriteShop> findByBuyerId(Long buyerId);
    Optional<FavoriteShop> findByBuyerIdAndShopId(Long buyerId, Long shopId);
    void deleteByBuyerIdAndShopId(Long buyerId, Long shopId);
}
