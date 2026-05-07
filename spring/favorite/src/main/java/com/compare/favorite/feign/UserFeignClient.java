package com.compare.favorite.feign;

import com.compare.core.result.R;
import com.compare.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "user-service", configuration = FeignConfig.class)
public interface UserFeignClient {

    @GetMapping("/api/buyer/{id}")
    R<Map<String, Object>> getBuyerById(@PathVariable Long id);

    @GetMapping("/api/seller/{id}")
    R<Map<String, Object>> getSellerById(@PathVariable Long id);
}
