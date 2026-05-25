package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 学生列表项 VO
 */
@Schema(description = "学生信息")
public class StudentVO {

    /** 主键ID */
    @Schema(description = "用户ID")
    private Long id;

    /** 姓名（对应 name 字段） */
    @Schema(description = "姓名")
    private String name;

    /** 角色 */
    @Schema(description = "角色")
    private String role;

    /** 手机号 */
    @Schema(description = "手机号")
    private String phone;

    /** 班级ID */
    @Schema(description = "班级ID")
    private Long classId;

    /** 性别：0未知 1男 2女 */
    @Schema(description = "性别：0未知 1男 2女")
    private Integer gender;

    /** 邮箱 */
    @Schema(description = "邮箱")
    private String email;

    /** 状态：1正常 0禁用 */
    @Schema(description = "状态：1正常 0禁用")
    private Integer status;

    /** 创建时间 */
    @Schema(description = "创建时间")
    private String createTime;

    // ======================== getters / setters ========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }

    public Integer getGender() { return gender; }
    public void setGender(Integer gender) { this.gender = gender; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
