package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/** 我的模板任务分页请求 */
@Schema(description = "我的模板任务分页请求")
public class MyTaskTemplatePageRequest {
    @NotNull @Min(1) @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED) private Integer pageNum;
    @NotNull @Min(1) @Schema(description = "每页条数", requiredMode = Schema.RequiredMode.REQUIRED) private Integer pageSize;
    @Schema(description = "用户ID，不传则用当前登录用户") private Long userId;
    @Schema(description = "模板名称（模糊）") private String templateName;
    @Schema(description = "任务类型") private String taskType;
    @Schema(description = "是否强制：1强制 0不强制") private Integer isMandatory;

    public Integer getPageNum() { return pageNum; } public void setPageNum(Integer v) { this.pageNum = v; }
    public Integer getPageSize() { return pageSize; } public void setPageSize(Integer v) { this.pageSize = v; }
    public Long getUserId() { return userId; } public void setUserId(Long v) { this.userId = v; }
    public String getTemplateName() { return templateName; } public void setTemplateName(String v) { this.templateName = v; }
    public String getTaskType() { return taskType; } public void setTaskType(String v) { this.taskType = v; }
    public Integer getIsMandatory() { return isMandatory; } public void setIsMandatory(Integer v) { this.isMandatory = v; }
}
