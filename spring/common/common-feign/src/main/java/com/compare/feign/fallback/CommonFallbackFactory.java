package com.compare.feign.fallback;

import com.compare.core.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

@Slf4j
public class CommonFallbackFactory<T> implements FallbackFactory<T> {

    private final Class<T> feignClientClass;

    public CommonFallbackFactory(Class<T> feignClientClass) {
        this.feignClientClass = feignClientClass;
    }

    @Override
    public T create(Throwable cause) {
        log.error("Feign call [{}] failed: {}", feignClientClass.getSimpleName(), cause.getMessage());
        return null;
    }
}
