package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 我的任务分页请求 DTO
 */
@Schema(description = "我的任务分页请求")
public class MyTaskPageRequest {

    /** 页码 */
    @NotNull @Min(1)
    @Schema(description = "页码", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNum;

    /** 每页条数 */
    @NotNull @Min(1)
    @Schema(description = "每页条数", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;

    /** 状态过滤：0未完成 1已完成，不传则全部 */
    @Schema(description = "状态：0未完成 1已完成，不传查全部")
    private String status;

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
