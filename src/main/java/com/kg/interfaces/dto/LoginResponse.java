package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 登录响应 DTO
 */
@Schema(description = "登录响应")
public class LoginResponse {

    /** JWT 令牌 */
    @Schema(description = "JWT 令牌")
    private String token;

    /** 用户 ID */
    @Schema(description = "用户 ID")
    private Long userId;

    /** 账号 */
    @Schema(description = "账号")
    private String account;

    /** 角色 */
    @Schema(description = "角色：teacher / headmaster / student")
    private String role;

    /**
     * 构建登录响应
     */
    public static LoginResponse of(String token, Long userId, String account, String role) {
        LoginResponse response = new LoginResponse();
        response.token = token;
        response.userId = userId;
        response.account = account;
        response.role = role;
        return response;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
