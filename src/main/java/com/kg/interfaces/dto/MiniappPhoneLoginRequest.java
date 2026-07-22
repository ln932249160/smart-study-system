package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;

@Schema(description = "小程序手机号登录请求")
public class MiniappPhoneLoginRequest {
    @NotBlank @Schema(description = "wx.login 获取的 code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String loginCode;
    @NotBlank @Schema(description = "getPhoneNumber 获取的 code", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phoneCode;

    public String getLoginCode() { return loginCode; } public void setLoginCode(String v) { this.loginCode = v; }
    public String getPhoneCode() { return phoneCode; } public void setPhoneCode(String v) { this.phoneCode = v; }
}
