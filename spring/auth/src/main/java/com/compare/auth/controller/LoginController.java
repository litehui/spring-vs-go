package com.compare.auth.controller;

import com.compare.core.result.R;
import com.compare.log.annotation.OperLog;
import com.compare.security.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class LoginController {

    private final AuthService authService;

    @OperLog(value = "买家登录", module = "auth")
    @PostMapping("/login/buyer")
    public R<Map<String, Object>> loginBuyer(@RequestBody LoginRequest request) {
        return R.ok(authService.loginBuyer(request.getUsername(), request.getPassword()));
    }

    @OperLog(value = "卖家登录", module = "auth")
    @PostMapping("/login/seller")
    public R<Map<String, Object>> loginSeller(@RequestBody LoginRequest request) {
        return R.ok(authService.loginSeller(request.getUsername(), request.getPassword()));
    }

    @OperLog(value = "卖家子账号登录", module = "auth")
    @PostMapping("/login/sub-account")
    public R<Map<String, Object>> loginSubAccount(@RequestBody LoginRequest request) {
        return R.ok(authService.loginSubAccount(request.getUsername(), request.getPassword()));
    }

    @OperLog(value = "注册", module = "auth")
    @PostMapping("/register")
    public R<Void> register(@RequestBody RegisterRequest request) {
        authService.register(request);
        return R.ok();
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        SecurityContextUtil.logout();
        return R.ok();
    }
}
