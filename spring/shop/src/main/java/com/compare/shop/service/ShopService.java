package com.compare.shop.service;

import com.compare.core.exception.BusinessException;
import com.compare.mq.producer.BaseMessageProducer;
import com.compare.shop.entity.Shop;
import com.compare.shop.repository.ShopRepository;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BaseMessageProducer messageProducer;

    @SentinelResource(value = "getShopById", blockHandler = "getShopByIdBlockHandler", fallback = "getShopByIdFallback")
    public Shop getById(Long id) {
        String cacheKey = "shop:" + id;
        Shop cached = (Shop) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        Shop shop = shopRepository.findById(id).orElseThrow(() -> new BusinessException("店铺不存在"));
        redisTemplate.opsForValue().set(cacheKey, shop, 30, TimeUnit.MINUTES);
        return shop;
    }

    public Shop getShopByIdBlockHandler(Long id, BlockException e) {
        throw new BusinessException(429, "店铺查询请求过于频繁");
    }

    public Shop getShopByIdFallback(Long id, Throwable t) {
        throw new BusinessException("店铺服务暂时不可用");
    }

    public List<Shop> listBySellerId(Long sellerId) {
        return shopRepository.findBySellerId(sellerId);
    }

    public Shop create(Shop shop) {
        Shop saved = shopRepository.save(shop);
        messageProducer.send("shopProducer-out-0", "shop-topic", saved);
        return saved;
    }

    public Shop update(Long id, Shop shop) {
        shopRepository.findById(id).orElseThrow(() -> new BusinessException("店铺不存在"));
        shop.setId(id);
        Shop saved = shopRepository.save(shop);
        redisTemplate.delete("shop:" + id);
        messageProducer.send("shopProducer-out-0", "shop-topic", saved);
        return saved;
    }

    public void delete(Long id) {
        shopRepository.deleteById(id);
        redisTemplate.delete("shop:" + id);
    }
}
