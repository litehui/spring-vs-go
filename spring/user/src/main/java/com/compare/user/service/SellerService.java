package com.compare.user.service;

import com.compare.core.exception.BusinessException;
import com.compare.mq.producer.BaseMessageProducer;
import com.compare.user.entity.Seller;
import com.compare.user.repository.SellerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BaseMessageProducer messageProducer;

    public Seller getById(Long id) {
        String cacheKey = "seller:" + id;
        Seller cached = (Seller) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        Seller seller = sellerRepository.findById(id).orElseThrow(() -> new BusinessException("卖家不存在"));
        redisTemplate.opsForValue().set(cacheKey, seller, 30, TimeUnit.MINUTES);
        return seller;
    }

    public Seller update(Long id, Seller seller) {
        sellerRepository.findById(id).orElseThrow(() -> new BusinessException("卖家不存在"));
        seller.setId(id);
        Seller saved = sellerRepository.save(seller);
        redisTemplate.delete("seller:" + id);
        messageProducer.send("userProducer-out-0", "user-topic", saved);
        return saved;
    }

    public void delete(Long id) {
        sellerRepository.deleteById(id);
        redisTemplate.delete("seller:" + id);
    }
}
