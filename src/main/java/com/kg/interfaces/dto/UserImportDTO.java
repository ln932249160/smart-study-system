package com.kg.interfaces.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 用户导入 Excel 行模型 —— EasyExcel 读写用。
 */
@Schema(description = "用户导入 Excel 行")
public class UserImportDTO {

    /** 姓名 */
    @Schema(description = "姓名")
    @ExcelProperty("姓名")
    private String name;

    /** 角色：2班长 3学生 */
    @Schema(description = "角色：2班长 3学生")
    @ExcelProperty("角色：2班长 3学生")
    private String role;

    /** 性别：0未知 1男 2女 */
    @Schema(description = "性别：1男 2女")
    @ExcelProperty("性别：1男 2女")
    private Integer gender;

    /** 手机号（同时作为登录账号 account） */
    @Schema(description = "手机号")
    @ExcelProperty("手机号")
    private String phone;

    /** 邮箱（可选） */
    @Schema(description = "邮箱（可选）")
    @ExcelProperty("邮箱")
    private String email;

    /** 描述（可选） */
    @Schema(description = "描述（可选）")
    @ExcelProperty("描述")
    private String description;

    /** 班级名称（可选） */
    @Schema(description = "班级名称（可选）")
    @ExcelProperty("班级名称")
    private String className;

    /** 账号（可选，不填默认用手机号） */
    @Schema(description = "账号（可选，不填默认用手机号）")
    @ExcelProperty("账号")
    private String account;

    // ======================== getters / setters ========================

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
}
