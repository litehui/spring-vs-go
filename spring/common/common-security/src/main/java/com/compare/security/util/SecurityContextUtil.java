package com.compare.security.util;

import cn.dev33.satoken.stp.StpUtil;

public class SecurityContextUtil {

    public static Long getCurrentUserId() {
        return StpUtil.getLoginIdAsLong();
    }

    public static String getCurrentUsername() {
        return (String) StpUtil.getSession().get("username");
    }

    public static String getCurrentUserRole() {
        return (String) StpUtil.getSession().get("role");
    }

    public static void login(Long userId, String username, String role) {
        StpUtil.login(userId);
        StpUtil.getSession().set("username", username);
        StpUtil.getSession().set("role", role);
    }

    public static void logout() {
        StpUtil.logout();
    }
}
