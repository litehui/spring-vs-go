package com.compare.favorite.service;

import com.compare.core.exception.BusinessException;
import com.compare.favorite.entity.FavoriteProduct;
import com.compare.favorite.repository.FavoriteProductRepository;
import com.compare.mq.producer.BaseMessageProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteProductService {

    private final FavoriteProductRepository favoriteProductRepository;
    private final BaseMessageProducer messageProducer;

    public List<FavoriteProduct> listByBuyerId(Long buyerId) {
        return favoriteProductRepository.findByBuyerId(buyerId);
    }

    public FavoriteProduct add(Long buyerId, Long productId) {
        favoriteProductRepository.findByBuyerIdAndProductId(buyerId, productId)
                .ifPresent(f -> { throw new BusinessException("已收藏该商品"); });
        FavoriteProduct fp = new FavoriteProduct();
        fp.setBuyerId(buyerId);
        fp.setProductId(productId);
        FavoriteProduct saved = favoriteProductRepository.save(fp);
        messageProducer.send("favoriteProducer-out-0", "favorite-topic", saved);
        return saved;
    }

    public void remove(Long buyerId, Long productId) {
        favoriteProductRepository.deleteByBuyerIdAndProductId(buyerId, productId);
    }
}
