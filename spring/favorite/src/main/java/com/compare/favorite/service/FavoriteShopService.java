package com.compare.favorite.service;

import com.compare.core.exception.BusinessException;
import com.compare.favorite.entity.FavoriteShop;
import com.compare.favorite.repository.FavoriteShopRepository;
import com.compare.mq.producer.BaseMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteShopService {

    private final FavoriteShopRepository favoriteShopRepository;
    private final BaseMessageProducer messageProducer;

    public List<FavoriteShop> listByBuyerId(Long buyerId) {
        return favoriteShopRepository.findByBuyerId(buyerId);
    }

    public FavoriteShop add(Long buyerId, Long shopId) {
        favoriteShopRepository.findByBuyerIdAndShopId(buyerId, shopId)
                .ifPresent(f -> { throw new BusinessException("已收藏该店铺"); });
        FavoriteShop fs = new FavoriteShop();
        fs.setBuyerId(buyerId);
        fs.setShopId(shopId);
        FavoriteShop saved = favoriteShopRepository.save(fs);
        messageProducer.send("favoriteProducer-out-0", "favorite-topic", saved);
        return saved;
    }

    public void remove(Long buyerId, Long shopId) {
        favoriteShopRepository.deleteByBuyerIdAndShopId(buyerId, shopId);
    }
}
