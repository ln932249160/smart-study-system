package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 学生分页查询请求 DTO
 */
@Schema(description = "学生分页查询请求")
public class StudentPageRequest {

    /** 姓名（模糊匹配 name 字段） */
    @Schema(description = "姓名（模糊匹配）", example = "张")
    private String name;

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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }

    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
