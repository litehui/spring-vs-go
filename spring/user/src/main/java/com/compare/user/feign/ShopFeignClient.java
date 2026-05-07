package com.compare.user.feign;

import com.compare.core.result.R;
import com.compare.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "shop-service", configuration = FeignConfig.class)
public interface ShopFeignClient {

    @GetMapping("/api/shop/seller/{sellerId}")
    R<Map<String, Object>> listBySellerId(@PathVariable Long sellerId);

    @GetMapping("/api/shop/{id}")
    R<Map<String, Object>> getById(@PathVariable Long id);
}
