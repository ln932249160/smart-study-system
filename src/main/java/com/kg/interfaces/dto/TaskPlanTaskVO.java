package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/** 任务计划下的单个任务 VO */
@Schema(description = "计划任务")
public class TaskPlanTaskVO {
    @Schema(description = "任务ID") private Long id;
    @Schema(description = "任务名称") private String taskName;
    @Schema(description = "计划日期") private String planDate;
    @Schema(description = "开始时间") private String taskStartTime;
    @Schema(description = "结束时间") private String taskEndTime;
    @Schema(description = "状态") private Integer status;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getTaskName() { return taskName; } public void setTaskName(String v) { this.taskName = v; }
    public String getPlanDate() { return planDate; } public void setPlanDate(String v) { this.planDate = v; }
    public String getTaskStartTime() { return taskStartTime; } public void setTaskStartTime(String v) { this.taskStartTime = v; }
    public String getTaskEndTime() { return taskEndTime; } public void setTaskEndTime(String v) { this.taskEndTime = v; }
    public Integer getStatus() { return status; } public void setStatus(Integer v) { this.status = v; }
}
