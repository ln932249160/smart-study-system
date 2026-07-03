package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/** 任务计划详情 VO */
@Schema(description = "任务计划详情")
public class TaskPlanDetailVO {
    @Schema(description = "计划ID") private Long id;
    @Schema(description = "计划名称") private String planName;
    @Schema(description = "任务类型") private String taskType;
    @Schema(description = "模板ID") private Long templateId;
    @Schema(description = "重复类型") private String repeatType;
    @Schema(description = "重复配置") private String repeatConfig;
    @Schema(description = "开始日期") private String startDate;
    @Schema(description = "结束日期") private String endDate;
    @Schema(description = "已生成任务数") private Integer generatedCount;
    @Schema(description = "状态") private Integer status;
    @Schema(description = "已生成任务列表") private List<TaskPlanTaskVO> tasks;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getPlanName() { return planName; } public void setPlanName(String v) { this.planName = v; }
    public String getTaskType() { return taskType; } public void setTaskType(String v) { this.taskType = v; }
    public Long getTemplateId() { return templateId; } public void setTemplateId(Long v) { this.templateId = v; }
    public String getRepeatType() { return repeatType; } public void setRepeatType(String v) { this.repeatType = v; }
    public String getRepeatConfig() { return repeatConfig; } public void setRepeatConfig(String v) { this.repeatConfig = v; }
    public String getStartDate() { return startDate; } public void setStartDate(String v) { this.startDate = v; }
    public String getEndDate() { return endDate; } public void setEndDate(String v) { this.endDate = v; }
    public Integer getGeneratedCount() { return generatedCount; } public void setGeneratedCount(Integer v) { this.generatedCount = v; }
    public Integer getStatus() { return status; } public void setStatus(Integer v) { this.status = v; }
    public List<TaskPlanTaskVO> getTasks() { return tasks; } public void setTasks(List<TaskPlanTaskVO> v) { this.tasks = v; }
}
