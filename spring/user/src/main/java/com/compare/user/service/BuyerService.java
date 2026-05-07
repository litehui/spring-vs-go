package com.compare.user.service;

import com.compare.core.exception.BusinessException;
import com.compare.mq.producer.BaseMessageProducer;
import com.compare.user.entity.Buyer;
import com.compare.user.repository.BuyerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class BuyerService {

    private final BuyerRepository buyerRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BaseMessageProducer messageProducer;

    public Buyer getById(Long id) {
        String cacheKey = "buyer:" + id;
        Buyer cached = (Buyer) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        Buyer buyer = buyerRepository.findById(id).orElseThrow(() -> new BusinessException("买家不存在"));
        redisTemplate.opsForValue().set(cacheKey, buyer, 30, TimeUnit.MINUTES);
        return buyer;
    }

    public Buyer update(Long id, Buyer buyer) {
        buyerRepository.findById(id).orElseThrow(() -> new BusinessException("买家不存在"));
        buyer.setId(id);
        Buyer saved = buyerRepository.save(buyer);
        redisTemplate.delete("buyer:" + id);
        messageProducer.send("userProducer-out-0", "user-topic", saved);
        return saved;
    }

    public void delete(Long id) {
        buyerRepository.deleteById(id);
        redisTemplate.delete("buyer:" + id);
    }
}
