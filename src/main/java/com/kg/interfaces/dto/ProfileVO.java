package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 个人信息 VO
 */
@Schema(description = "个人信息")
public class ProfileVO {

    @Schema(description = "用户ID") private Long id;
    @Schema(description = "账号") private String account;
    @Schema(description = "姓名") private String name;
    @Schema(description = "角色") private String role;
    @Schema(description = "性别：0未知 1男 2女") private Integer gender;
    @Schema(description = "邮箱") private String email;
    @Schema(description = "手机号") private String phone;
    @Schema(description = "描述") private String description;
    @Schema(description = "班级ID") private Long classId;
    @Schema(description = "班级名称") private String className;
    @Schema(description = "状态") private Integer status;
    @Schema(description = "创建时间") private String createTime;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
