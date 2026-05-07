package com.compare.feign.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.slf4j.MDC;

public class FeignRequestInterceptor implements RequestInterceptor {

    private static final String TRACE_ID = "traceId";
    private static final String SATOKEN_HEADER = "satoken";

    @Override
    public void apply(RequestTemplate template) {
        String tokenValue = StpUtil.getTokenValue();
        if (tokenValue != null) {
            template.header(SATOKEN_HEADER, tokenValue);
        }
        String traceId = MDC.get(TRACE_ID);
        if (traceId != null) {
            template.header(TRACE_ID, traceId);
        }
    }
}
