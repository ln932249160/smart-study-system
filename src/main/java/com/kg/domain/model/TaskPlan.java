package com.kg.domain.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

/** 重复任务计划领域模型 —— 字段来源于 task_plan 表。 */
public class TaskPlan {
    private Long id;
    private String planName;
    private String taskType;
    private Long templateId;
    private Integer isMandatory;
    private String taskDescription;
    private Integer priority;
    private String repeatType;
    /** 规则配置JSON字符串 */
    private String repeatConfig;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer targetType;
    private String targetIds;
    private Integer generatedCount;
    private Integer status;
    private Long createBy;
    private LocalDateTime createTime;
    private Long updateBy;
    private LocalDateTime updateTime;

    public Long getId() { return id; } public void setId(Long v) { this.id = v; }
    public String getPlanName() { return planName; } public void setPlanName(String v) { this.planName = v; }
    public String getTaskType() { return taskType; } public void setTaskType(String v) { this.taskType = v; }
    public Long getTemplateId() { return templateId; } public void setTemplateId(Long v) { this.templateId = v; }
    public Integer getIsMandatory() { return isMandatory; } public void setIsMandatory(Integer v) { this.isMandatory = v; }
    public String getTaskDescription() { return taskDescription; } public void setTaskDescription(String v) { this.taskDescription = v; }
    public Integer getPriority() { return priority; } public void setPriority(Integer v) { this.priority = v; }
    public String getRepeatType() { return repeatType; } public void setRepeatType(String v) { this.repeatType = v; }
    public String getRepeatConfig() { return repeatConfig; } public void setRepeatConfig(String v) { this.repeatConfig = v; }
    public LocalDate getStartDate() { return startDate; } public void setStartDate(LocalDate v) { this.startDate = v; }
    public LocalDate getEndDate() { return endDate; } public void setEndDate(LocalDate v) { this.endDate = v; }
    public Integer getTargetType() { return targetType; } public void setTargetType(Integer v) { this.targetType = v; }
    public String getTargetIds() { return targetIds; } public void setTargetIds(String v) { this.targetIds = v; }
    public Integer getGeneratedCount() { return generatedCount; } public void setGeneratedCount(Integer v) { this.generatedCount = v; }
    public Integer getStatus() { return status; } public void setStatus(Integer v) { this.status = v; }
    public Long getCreateBy() { return createBy; } public void setCreateBy(Long v) { this.createBy = v; }
    public LocalDateTime getCreateTime() { return createTime; } public void setCreateTime(LocalDateTime v) { this.createTime = v; }
    public Long getUpdateBy() { return updateBy; } public void setUpdateBy(Long v) { this.updateBy = v; }
    public LocalDateTime getUpdateTime() { return updateTime; } public void setUpdateTime(LocalDateTime v) { this.updateTime = v; }
}
