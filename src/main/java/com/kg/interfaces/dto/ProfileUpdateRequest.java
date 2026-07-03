package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 修改个人资料请求 DTO
 */
@Schema(description = "修改个人资料")
public class ProfileUpdateRequest {

    @Schema(description = "姓名") private String name;
    @Schema(description = "性别：0未知 1男 2女") private Integer gender;
    @Schema(description = "邮箱") private String email;
    @Schema(description = "手机号") private String phone;
    @Schema(description = "描述") private String description;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
