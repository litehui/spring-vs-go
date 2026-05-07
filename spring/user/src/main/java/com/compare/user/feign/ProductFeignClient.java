package com.compare.user.feign;

import com.compare.core.result.R;
import com.compare.feign.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.Map;

@FeignClient(name = "product-service", configuration = FeignConfig.class)
public interface ProductFeignClient {

    @GetMapping("/api/product/seller/{sellerId}")
    R<Map<String, Object>> listBySellerId(@PathVariable Long sellerId);

    @GetMapping("/api/product/{id}")
    R<Map<String, Object>> getById(@PathVariable Long id);
}
