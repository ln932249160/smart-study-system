package com.kg.interfaces.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 任务列表项 VO
 */
@Schema(description = "任务信息")
public class TaskVO {

    @Schema(description = "任务ID")
    private Long id;
    @Schema(description = "任务名称")
    private String taskName;
    @Schema(description = "任务类型")
    private String taskType;
    @Schema(description = "是否模板")
    private Integer isTemplate;
    @Schema(description = "模板任务ID")
    private Long templateTaskId;
    @Schema(description = "轮次")
    private Integer roundNo;
    @Schema(description = "是否强制")
    private Integer isMandatory;
    @Schema(description = "任务描述")
    private String taskDescription;
    @Schema(description = "开始时间")
    private String taskStartTime;
    @Schema(description = "结束时间")
    private String taskEndTime;
    @Schema(description = "优先级")
    private Integer priority;
    @Schema(description = "班级ID")
    private Long classId;
    @Schema(description = "已完成人数")
    private Integer completedCount;
    @Schema(description = "未完成人数")
    private Integer uncompletedCount;
    @Schema(description = "完成百分比（0-100）")
    private Integer completionPercent;
    @Schema(description = "创建时间")
    private String createTime;

    // ======================== getters / setters ========================

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getTaskType() { return taskType; }
    public void setTaskType(String taskType) { this.taskType = taskType; }
    public Integer getIsTemplate() { return isTemplate; }
    public void setIsTemplate(Integer isTemplate) { this.isTemplate = isTemplate; }
    public Long getTemplateTaskId() { return templateTaskId; }
    public void setTemplateTaskId(Long templateTaskId) { this.templateTaskId = templateTaskId; }
    public Integer getRoundNo() { return roundNo; }
    public void setRoundNo(Integer roundNo) { this.roundNo = roundNo; }
    public Integer getIsMandatory() { return isMandatory; }
    public void setIsMandatory(Integer isMandatory) { this.isMandatory = isMandatory; }
    public String getTaskDescription() { return taskDescription; }
    public void setTaskDescription(String taskDescription) { this.taskDescription = taskDescription; }
    public String getTaskStartTime() { return taskStartTime; }
    public void setTaskStartTime(String taskStartTime) { this.taskStartTime = taskStartTime; }
    public String getTaskEndTime() { return taskEndTime; }
    public void setTaskEndTime(String taskEndTime) { this.taskEndTime = taskEndTime; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Long getClassId() { return classId; }
    public void setClassId(Long classId) { this.classId = classId; }
    public Integer getCompletedCount() { return completedCount; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }
    public Integer getUncompletedCount() { return uncompletedCount; }
    public void setUncompletedCount(Integer uncompletedCount) { this.uncompletedCount = uncompletedCount; }
    public Integer getCompletionPercent() { return completionPercent; }
    public void setCompletionPercent(Integer completionPercent) { this.completionPercent = completionPercent; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
