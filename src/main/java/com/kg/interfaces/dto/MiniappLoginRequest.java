package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(description = "小程序快捷登录请求")
public class MiniappLoginRequest {
    @NotBlank @Schema(description = "wx.login 获取的 code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginCode;

    public String getLoginCode() { return loginCode; }
    public void setLoginCode(String v) { this.loginCode = v; }
}
