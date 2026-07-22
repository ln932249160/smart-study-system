package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "小程序登录响应")
public class MiniappLoginVO {
    @Schema(description = "是否需要手机号授权") private boolean needPhoneAuth;
    @Schema(description = "系统JWT") private String token;
    @Schema(description = "用户信息") private UserInfo userInfo;

    @Schema(description = "用户信息")
    public static class UserInfo {
        @Schema(description = "用户ID") private Long id;
        @Schema(description = "账号") private String account;
        @Schema(description = "姓名") private String name;
        @Schema(description = "角色") private String role;
        @Schema(description = "班级ID") private Long classId;
        @Schema(description = "手机号") private String phone;

        public Long getId() { return id; } public void setId(Long v) { this.id = v; }
        public String getAccount() { return account; } public void setAccount(String v) { this.account = v; }
        public String getName() { return name; } public void setName(String v) { this.name = v; }
        public String getRole() { return role; } public void setRole(String v) { this.role = v; }
        public Long getClassId() { return classId; } public void setClassId(Long v) { this.classId = v; }
        public String getPhone() { return phone; } public void setPhone(String v) { this.phone = v; }
    }

    public boolean isNeedPhoneAuth() { return needPhoneAuth; } public void setNeedPhoneAuth(boolean v) { this.needPhoneAuth = v; }
    public String getToken() { return token; } public void setToken(String v) { this.token = v; }
    public UserInfo getUserInfo() { return userInfo; } public void setUserInfo(UserInfo v) { this.userInfo = v; }
}
