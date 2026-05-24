package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

/**
 * 登录请求 DTO
 */
@Schema(description = "登录请求")
public class LoginRequest {

    /** 账号 */
    @NotBlank(message = "账号不能为空")
    @Schema(description = "账号", example = "root", requiredMode = Schema.RequiredMode.REQUIRED)
    private String account;

    /** 密码 */
    @NotBlank(message = "密码不能为空")
    @Schema(description = "密码", example = "000000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
