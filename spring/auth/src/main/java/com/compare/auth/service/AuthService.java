package com.compare.auth.service;

import cn.dev33.satoken.stp.StpUtil;
import com.compare.core.exception.BusinessException;
import com.compare.security.util.SecurityContextUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Map<String, Object> loginBuyer(String username, String password) {
        Map<String, Object> user = findUser("buyer", username);
        validatePassword(user, password);
        SecurityContextUtil.login(((Number) user.get("id")).longValue(), username, "buyer");
        return Map.of(
                "token", StpUtil.getTokenValue(),
                "userId", user.get("id"),
                "username", username,
                "role", "buyer"
        );
    }

    public Map<String, Object> loginSeller(String username, String password) {
        Map<String, Object> user = findUser("seller", username);
        validatePassword(user, password);
        SecurityContextUtil.login(((Number) user.get("id")).longValue(), username, "seller");
        return Map.of(
                "token", StpUtil.getTokenValue(),
                "userId", user.get("id"),
                "username", username,
                "role", "seller"
        );
    }

    public Map<String, Object> loginSubAccount(String username, String password) {
        Map<String, Object> user = findUser("seller_sub_account", username);
        validatePassword(user, password);
        SecurityContextUtil.login(((Number) user.get("id")).longValue(), username, "sub_account");
        return Map.of(
                "token", StpUtil.getTokenValue(),
                "userId", user.get("id"),
                "sellerId", user.get("seller_id"),
                "username", username,
                "role", "sub_account"
        );
    }

    public void register(com.compare.auth.controller.RegisterRequest request) {
        String userType = request.getUserType();
        if ("buyer".equals(userType)) {
            registerBuyer(request);
        } else if ("seller".equals(userType)) {
            registerSeller(request);
        } else {
            throw new BusinessException("无效的用户类型: " + userType);
        }
    }

    private void registerBuyer(com.compare.auth.controller.RegisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        jdbcTemplate.update(
                "INSERT INTO buyer (username, password, nickname, phone, email) VALUES (?, ?, ?, ?, ?)",
                request.getUsername(), encodedPassword, request.getNickname(),
                request.getPhone(), request.getEmail()
        );
    }

    private void registerSeller(com.compare.auth.controller.RegisterRequest request) {
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        jdbcTemplate.update(
                "INSERT INTO seller (username, password, company_name, contact_name, phone, email, business_license) VALUES (?, ?, ?, ?, ?, ?, ?)",
                request.getUsername(), encodedPassword, request.getCompanyName(),
                request.getContactName(), request.getPhone(), request.getEmail(),
                request.getBusinessLicense()
        );
    }

    private Map<String, Object> findUser(String table, String username) {
        var results = jdbcTemplate.queryForList("SELECT * FROM " + table + " WHERE username = ?", username);
        if (results.isEmpty()) {
            throw new BusinessException("用户不存在");
        }
        return results.get(0);
    }

    private void validatePassword(Map<String, Object> user, String rawPassword) {
        String encodedPassword = (String) user.get("password");
        if (!passwordEncoder.matches(rawPassword, encodedPassword)) {
            throw new BusinessException("密码错误");
        }
    }
}
