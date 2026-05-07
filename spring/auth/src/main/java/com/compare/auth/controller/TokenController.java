package com.compare.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.compare.core.result.R;
import com.compare.security.util.SecurityContextUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/token")
public class TokenController {

    @GetMapping("/info")
    public R<Map<String, Object>> getTokenInfo() {
        Long userId = SecurityContextUtil.getCurrentUserId();
        String username = SecurityContextUtil.getCurrentUsername();
        String role = SecurityContextUtil.getCurrentUserRole();
        return R.ok(Map.of("userId", userId, "username", username, "role", role));
    }

    @PostMapping("/refresh")
    public R<Map<String, Object>> refreshToken() {
        Object loginId = StpUtil.getLoginId();
        String newToken = StpUtil.getTokenValue();
        return R.ok(Map.of("token", newToken));
    }
}
