package com.compare.product.service;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.compare.core.exception.BusinessException;
import com.compare.mq.producer.BaseMessageProducer;
import com.compare.product.entity.Product;
import com.compare.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BaseMessageProducer messageProducer;

    @SentinelResource(value = "getProductById", blockHandler = "getProductBlockHandler", fallback = "getProductFallback")
    public Product getById(Long id) {
        String cacheKey = "product:" + id;
        Product cached = (Product) redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            return cached;
        }
        Product product = productRepository.findById(id).orElseThrow(() -> new BusinessException("商品不存在"));
        redisTemplate.opsForValue().set(cacheKey, product, 30, TimeUnit.MINUTES);
        return product;
    }

    public Product getProductBlockHandler(Long id, BlockException e) {
        throw new BusinessException(429, "商品查询请求过于频繁");
    }

    public Product getProductFallback(Long id, Throwable t) {
        throw new BusinessException("商品服务暂时不可用");
    }

    public List<Product> listBySellerId(Long sellerId) {
        return productRepository.findBySellerId(sellerId);
    }

    public List<Product> listByShopId(Long shopId) {
        return productRepository.findByShopId(shopId);
    }

    public List<Product> listByCategory(String category) {
        return productRepository.findByCategory(category);
    }

    public Product create(Product product) {
        Product saved = productRepository.save(product);
        messageProducer.send("productProducer-out-0", "product-topic", saved);
        return saved;
    }

    public Product update(Long id, Product product) {
        productRepository.findById(id).orElseThrow(() -> new BusinessException("商品不存在"));
        product.setId(id);
        Product saved = productRepository.save(product);
        redisTemplate.delete("product:" + id);
        messageProducer.send("productProducer-out-0", "product-topic", saved);
        return saved;
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
        redisTemplate.delete("product:" + id);
    }

    public Product updateStock(Long id, int quantity) {
        Product product = productRepository.findById(id).orElseThrow(() -> new BusinessException("商品不存在"));
        product.setStock(product.getStock() + quantity);
        if (product.getStock() < 0) {
            throw new BusinessException("库存不足");
        }
        Product saved = productRepository.save(product);
        redisTemplate.delete("product:" + id);
        messageProducer.send("productProducer-out-0", "product-topic", saved);
        return saved;
    }
}
