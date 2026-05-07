package com.compare.auth.controller;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String nickname;
    private String phone;
    private String email;
    @NotBlank(message = "用户类型不能为空")
    private String userType;
    private String companyName;
    private String contactName;
    private String businessLicense;
}
