package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;

/**
 * 编辑学生请求 DTO
 */
@Schema(description = "编辑学生请求")
public class StudentUpdateRequest {

    /** 姓名（更新 name 字段） */
    @Schema(description = "姓名", example = "张三改")
    private String name;

    /** 账号（登录账号） */
    @Schema(description = "账号")
    private String account;

    /** 角色：student / headmaster */
    @Schema(description = "角色", example = "student")
    private String role;

    /** 性别：0未知 1男 2女 */
    @Schema(description = "性别", example = "1")
    private Integer gender;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 描述 */
    @Schema(description = "描述")
    private String description;

    @Schema(description = "教师备注（仅老师可填）")
    private String teacherRemark;

    /** 班级ID */
    @Schema(description = "班级ID")
    private Long classId;

    /** 状态：1正常 0禁用 */
    @Schema(description = "状态：1正常 0禁用")
    private Integer status;

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

    public String getTeacherRemark() { return teacherRemark; }
    public void setTeacherRemark(String s) { this.teacherRemark = s; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
