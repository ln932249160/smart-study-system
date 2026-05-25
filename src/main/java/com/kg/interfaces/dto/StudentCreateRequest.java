package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 新增学生请求 DTO
 */
@Schema(description = "新增学生请求")
public class StudentCreateRequest {

    /** 姓名（对应 name 字段） */
    @Schema(description = "姓名", example = "张三", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    /** 账号（非必填，不填默认使用手机号） */
    @Schema(description = "账号（非必填，默认使用手机号）", example = "zhangsan")
    private String account;

    /** 角色：student / headmaster */
    @NotBlank(message = "角色不能为空")
    @Schema(description = "角色：student / headmaster", example = "student", requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;

    /** 性别：0未知 1男 2女 */
    @NotNull(message = "性别不能为空")
    @Schema(description = "性别：0未知 1男 2女", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer gender;

    /** 邮箱（非必填） */
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    /** 手机号 */
    @NotBlank(message = "手机号不能为空")
    @Schema(description = "手机号", example = "13800138000", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    /** 描述（非必填） */
    @Schema(description = "描述", example = "新生入学")
    private String description;

    /** 班级ID */
    @NotNull(message = "班级ID不能为空")
    @Schema(description = "班级ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long classId;

    // ======================== getters / setters ========================

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }

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
}
