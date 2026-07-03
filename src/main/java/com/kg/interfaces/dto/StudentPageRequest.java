package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 用户分页查询请求 DTO
 */
@Schema(description = "用户分页查询请求")
public class StudentPageRequest {

    /** 姓名（模糊匹配 name 字段） */
    @Schema(description = "姓名（模糊匹配）", example = "张")
    private String name;

    /** 角色 */
    @Schema(description = "角色：1老师 2班长 3学生")
    private String role;

    /** 手机号（模糊匹配） */
    @Schema(description = "手机号（模糊匹配）")
    private String phone;

    /** 用户ID（精确匹配） */
    @Schema(description = "用户ID（精确匹配）")
    private Long userId;

    /** 班级ID（精确匹配） */
    @Schema(description = "班级ID（精确匹配）")
    private Long classId;

    /** 班级名称（模糊匹配，优先于 classId） */
    @Schema(description = "班级名称（模糊匹配）")
    private String className;

    /** 页码（从 1 开始） */
    @NotNull(message = "页码不能为空")
    @Min(value = 1, message = "页码最小为 1")
    @Schema(description = "页码（从 1 开始）", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNum;

    /** 每页条数 */
    @NotNull(message = "每页条数不能为空")
    @Min(value = 1, message = "每页条数至少为 1")
    @Schema(description = "每页条数", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;

    // ======================== getters / setters ========================

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
