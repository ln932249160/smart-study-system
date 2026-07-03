package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/** 任务分页查询请求 */
@Schema(description = "任务分页查询请求")
public class TaskPageRequest {
    @NotNull @Min(1) @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED) private Integer pageNum;
    @NotNull @Min(1) @Schema(description = "每页条数", requiredMode = Schema.RequiredMode.REQUIRED) private Integer pageSize;
    @Schema(description = "任务类型") private String taskType;
    @Schema(description = "任务名称（模糊）") private String taskName;
    @Schema(description = "是否强制") private Integer isMandatory;
    @Schema(description = "开始时间起") private String startTimeBegin;
    @Schema(description = "开始时间止") private String startTimeEnd;
    @Schema(description = "结束时间起") private String endTimeBegin;
    @Schema(description = "结束时间止") private String endTimeEnd;

    public Integer getPageNum() { return pageNum; } public void setPageNum(Integer i) { this.pageNum = i; }
    public Integer getPageSize() { return pageSize; } public void setPageSize(Integer i) { this.pageSize = i; }
    public String getTaskType() { return taskType; } public void setTaskType(String s) { this.taskType = s; }
    public String getTaskName() { return taskName; } public void setTaskName(String s) { this.taskName = s; }
    public Integer getIsMandatory() { return isMandatory; } public void setIsMandatory(Integer i) { this.isMandatory = i; }
    public String getStartTimeBegin() { return startTimeBegin; } public void setStartTimeBegin(String s) { this.startTimeBegin = s; }
    public String getStartTimeEnd() { return startTimeEnd; } public void setStartTimeEnd(String s) { this.startTimeEnd = s; }
    public String getEndTimeBegin() { return endTimeBegin; } public void setEndTimeBegin(String s) { this.endTimeBegin = s; }
    public String getEndTimeEnd() { return endTimeEnd; } public void setEndTimeEnd(String s) { this.endTimeEnd = s; }
}
