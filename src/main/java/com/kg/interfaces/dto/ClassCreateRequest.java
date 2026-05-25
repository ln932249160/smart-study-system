package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 新增班级请求 DTO
 */
@Schema(description = "新增班级请求")
public class ClassCreateRequest {

    /** 班级名称 */
    @NotBlank(message = "班级名称不能为空")
    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String className;

    /** 班级描述（非必填） */
    @Schema(description = "班级描述")
    private String description;

    /** 选中的学生ID列表 */
    @Schema(description = "选中的学生ID列表")
    private List<Long> studentIds;

    // ======================== getters / setters ========================

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Long> getStudentIds() { return studentIds; }
    public void setStudentIds(List<Long> studentIds) { this.studentIds = studentIds; }
}
