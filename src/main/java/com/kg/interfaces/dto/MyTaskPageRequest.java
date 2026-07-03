package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 我的任务分页请求 DTO
 */
@Schema(description = "我的任务分页请求")
public class MyTaskPageRequest {

    @NotNull @Min(1)
    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageNum;

    @NotNull @Min(1)
    @Schema(description = "每页条数", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer pageSize;

    @Schema(description = "用户ID，不传则用当前登录用户")
    private Long userId;

    @Schema(description = "状态：0未完成 1已完成")
    private String status;

    @Schema(description = "任务名称（模糊）")
    private String taskName;

    @Schema(description = "任务类型：0每日复盘 1打卡 2学习 3刷题")
    private String taskType;

    @Schema(description = "是否强制：1强制 0不强制")
    private Integer forceFlag;

    @Schema(description = "开始时间起")
    private String startTimeBegin;

    @Schema(description = "开始时间止")
    private String startTimeEnd;

    @Schema(description = "结束时间起")
    private String endTimeBegin;

    @Schema(description = "结束时间止")
    private String endTimeEnd;

    @Schema(description = "完成时间起")
    private String finishTimeStart;

    @Schema(description = "完成时间止")
    private String finishTimeEnd;

    @Schema(description = "是否模板任务：1是 0否")
    private Integer isTemplate;

    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer i) { this.pageNum = i; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer i) { this.pageSize = i; }
    public Long getUserId() { return userId; }
    public void setUserId(Long l) { this.userId = l; }
    public String getStatus() { return status; }
    public void setStatus(String s) { this.status = s; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String s) { this.taskName = s; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String s) { this.taskType = s; }
    public Integer getForceFlag() { return forceFlag; }
    public void setForceFlag(Integer i) { this.forceFlag = i; }
    public String getStartTimeBegin() { return startTimeBegin; }
    public void setStartTimeBegin(String s) { this.startTimeBegin = s; }
    public String getStartTimeEnd() { return startTimeEnd; }
    public void setStartTimeEnd(String s) { this.startTimeEnd = s; }
    public String getEndTimeBegin() { return endTimeBegin; }
    public void setEndTimeBegin(String s) { this.endTimeBegin = s; }
    public String getEndTimeEnd() { return endTimeEnd; }
    public void setEndTimeEnd(String s) { this.endTimeEnd = s; }
    public String getFinishTimeStart() { return finishTimeStart; }
    public void setFinishTimeStart(String s) { this.finishTimeStart = s; }
    public String getFinishTimeEnd() { return finishTimeEnd; }
    public void setFinishTimeEnd(String s) { this.finishTimeEnd = s; }
    public Integer getIsTemplate() { return isTemplate; }
    public void setIsTemplate(Integer v) { this.isTemplate = v; }
}
